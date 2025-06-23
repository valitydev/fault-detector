package dev.vality.faultdetector.endpoint;

import dev.vality.damsel.fault_detector.FaultDetectorSrv;
import dev.vality.woody.thrift.impl.http.THServiceBuilder;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@WebServlet("/v1/fault-detector")
public class FaultDetectorEndpoint extends GenericServlet {

    private Servlet thriftServlet;

    @Autowired
    private FaultDetectorSrv.Iface faultDetectorService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(FaultDetectorSrv.Iface.class, faultDetectorService);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
