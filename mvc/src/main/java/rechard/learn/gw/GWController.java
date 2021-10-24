package rechard.learn.gw;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Rechard
 **/
public class GWController extends ServletWrappingController {



    public GWController() {
        //afterPropertiesSet() 会将GWServlet.class初始化后赋给分类的ServletInstance
        setServletClass(GWServlet.class);
        setServletName("gw");
        setSupportedMethods((String[]) null); // Allow all
    }


    /**
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        //转给this.servletInstance.service(request, response);来出来
        return super.handleRequestInternal(request, response);
    }

}
