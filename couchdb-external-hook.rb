#! /usr/bin/ruby

# This is the external process hook script that forwards requests from CouchDB
# to couchdb-lucene. This is a rewrite from the original hook from John Wood.
#
# It requires the following:
# * A Ruby interpreter (http://www.ruby-lang.org/en/downloads/)
# * The RubyGems package management system (http://docs.rubygems.org/read/chapter/3)
# * The 'json' ruby gem (sudo gem install json)
# * couchdb-external-hook.rb should be chmoded executeable
#
# == Configuration options
# couchdb.lucene.uri=http://localhost:5985/ - The URI where couchdb-lucene is
#    running. (default is http://localhost:5985/)
# couchdb.logdir=/some/log/dir - The directory you wish to use to store the
#    logs from this script (default is /tmp)
# couchdb.lucene.compression=true - Use GZIP-Compression to talk with
#    couchdb-lucene. (default is false)
# couchdb.lucene.debug=false - Let this hook be more chatty :)
#    (default is false)
#
# == Usage
# Example configuration in your CouchDB external section:
# fti=/usr/lib/couchdb/couchdb-lucene/couchdb-external-hook.rb couchdb.lucene.uri=http://localhost:5985/
#
# == Author
# Sebastian Cohnen
# http://twitter.com/tisba
# http://github.com/tisba

=begin
  TODO
  - add support for http-proxies
  - add test-suite
=end
require 'net/http'
require 'cgi'
require 'uri'
require 'zlib'

require 'rubygems'
require 'json'

class CouchDBHook
  class << self
    def dispatch(request)
      request = JSON.parse(request)
      request_uri = URI.join(lucene_uri.to_s, query_string(request))
      log "request to couchdb-lucene: #{request_uri.to_s}"

      # disable gzip compression if requested
      request['headers'].delete("Accept-Encoding") unless use_compression?

      resp, data = lucene_connection.get("#{request_uri.path}?#{request_uri.query}", request['headers']) 
      data = deflate(data) if use_compression?

      log "couchdb-lucene response: (#{resp.code})"
      debug "couchdb-lucene response: (#{data})"

      # building response to couchdb, strip content-encoding
      resp.delete("Content-Encoding")
      headers = {}
      resp.each_capitalized {|k,v| headers[k] = v}

      response = {"code"  => resp.code, "headers" => headers}
      if resp.content_type =~ /json/
        response['json'] = data
      else   
        response['body'] = data
      end

      response.to_json
    end

    def query_string(request)
      # decide, if we want to search or info
      lucene_query = if request['query']['q'].nil?
        "/info/"
      else
        "/search/"
      end

      # drop _fti from path, and rebuild
      request['path'].delete_at(1)
      lucene_query << request['path'].join('/') << "?"

      # rebuild rest of the query
      request['query'].each do |name, value|
        lucene_query << "#{name}=#{CGI::escape(value)}&"
      end
      lucene_query.chop!   

      lucene_query
    end

    def log(message)
      t = Time.now
      logfile << "#{t.strftime("%Y-%m-%d %H:%M:%S")}:#{t.usec} :: #{message} \n"
      logfile.flush
    end
    
    def debug(message)
      log(message) if debug?
    end
    

    def lucene_connection
      @connection ||= Net::HTTP.new(lucene_uri.host, lucene_uri.port)
    end

    def logdir
      @logdir ||= (args['couchdb.lucene.logdir'] || '/tmp')
    end
    
    def lucene_uri
      @lucene_uri ||= URI.parse(args['couchdb.lucene.uri'] || 'http://localhost:5985/')
    end

    def use_compression?
      @compression ||= (args['couchdb.lucene.compression'] == 'true')
    end

    def debug?
      @debug ||= (args['couchdb.lucene.debug'] == 'true')      
    end

    def logfile
      @log ||= File.new(File.join([logdir, args['couchdb.lucene.logfile'] || 'couchdb-external-hook.log']), 'a')
    end
    
    def args
      @arg_array ||= begin
        args = {}
        ARGV.each do |token|
          key, value = token.strip.split('=')
          args[key] = value
        end
        args
      end
    end
    
    def deflate(string)
      gz = Zlib::GzipReader.new(StringIO.new(string))
      gz.read
    rescue
      string
    end
  end
end

# Say hello!
CouchDBHook.log "Using couchdb-lucene at #{CouchDBHook.lucene_uri}"
CouchDBHook.log "Logfile: #{CouchDBHook.logfile.path} (DEBUG = #{CouchDBHook.debug? ? 'yes':'no'})"
CouchDBHook.log "Using compression to talk with couchdb-lucene" if CouchDBHook.use_compression?
CouchDBHook.log "Searcher started. Waiting for incoming requests..."

# Wait for requests from couchdb
while (line = STDIN.gets)
  CouchDBHook.debug "request from couchdb: #{line}"
  out = CouchDBHook.dispatch(line)
  CouchDBHook.debug "resopnse to couchdb: #{out}"
  puts out
  STDOUT.flush
end