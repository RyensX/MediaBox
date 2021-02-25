package com.skyd.imomoe.util.dlna.dms;

interface IResourceServerFactory {
    int getPort();

    IResourceServer getInstance();

    // ----------------------------------------------------------------------------
    // ---- implement
    // ----------------------------------------------------------------------------
    final class DefaultResourceServerFactoryImpl implements IResourceServerFactory {
        private final int port;

        public DefaultResourceServerFactoryImpl(int port) {
            this.port = port;
        }

        @Override
        public int getPort() {
            return port;
        }

        @Override
        public IResourceServer getInstance() {
            // TODO:
            // return new JettyHttpServer(port);
            return new NanoHttpServer(port);
        }
    }
}
