package com.skyd.imomoe.util.dlna.dms


internal interface IResourceServerFactory {
    val port: Int
    val instance: IResourceServer

    // ----------------------------------------------------------------------------
    // ---- implement
    // ----------------------------------------------------------------------------
    class DefaultResourceServerFactoryImpl(override val port: Int) : IResourceServerFactory {
        override val instance: IResourceServer
            // TODO:
            // return new JettyHttpServer(port);
            get() = NanoHttpServer(port)
    }
}