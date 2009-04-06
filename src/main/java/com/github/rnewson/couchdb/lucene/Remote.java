package com.github.rnewson.couchdb.lucene;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.store.NIOFSDirectory;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.remoting.rmi.RmiServiceExporter;

public final class Remote {

    public static void main(String[] args) throws Exception {
        final IndexReader reader = IndexReader.open(NIOFSDirectory.getDirectory("/home/rnewson/Source/couchdb/lucene"),
                true);
        final IndexSearcher searcher = new IndexSearcher(reader);

        final RmiServiceExporter exporter = new RmiServiceExporter();
        exporter.setAlwaysCreateRegistry(true);
        exporter.setRegistryPort(9999);
        exporter.setServiceName("SearchService");
        exporter.setService(searcher);
        exporter.setServiceInterface(Searchable.class);
        exporter.afterPropertiesSet();

        final RmiProxyFactoryBean invoker = new RmiProxyFactoryBean();
        invoker.setRefreshStubOnConnectFailure(true);
        invoker.setServiceInterface(Searchable.class);
        invoker.setServiceUrl("rmi://localhost:9999/SearchService");
        invoker.afterPropertiesSet();

        final Searchable searchable = (Searchable) invoker.getObject();

        System.err.println(searchable.maxDoc());

        exporter.destroy();
    }

}
