package my.weatherApp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import my.weatherApp.dao.MainMongoDao;
import my.weatherApp.service.ErrorService;

@Theme("mytheme")
public class MyUI extends UI {

    private static final Logger LOG = LoggerFactory.getLogger(MyUI.class);
    private DashboardForm dashboardForm = new DashboardForm();
    private static final String uitheme = "v-ui-my";
    private static final String errorTheme = "v-label-error";
    private ErrorService errorService = ErrorService.getInstance();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        LOG.info("UI init ");
        Label errorLabel = errorService.getLabel();
        errorLabel.setStyleName(errorTheme);
        errorLabel.setWidth(dashboardForm.getWidth(), dashboardForm.getWidthUnits());
        setErrorHandler(errorService);

        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName(uitheme);
        layout.setSizeFull();

        dashboardForm.getDashBoard(vaadinRequest.getRemoteAddr());

        layout.addComponents(dashboardForm, errorLabel);
        layout.setComponentAlignment(dashboardForm, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(errorLabel, Alignment.BOTTOM_CENTER);

        setContent(layout);
    }

    @WebServlet(urlPatterns = { "/*", "/VAADIN/*" }, name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {

        private static final Logger LOG = LoggerFactory.getLogger(MyUIServlet.class);
        private MainMongoDao mongoDao;

        @Override
        public void init() throws ServletException {
            LOG.info("Servlet init()");
            mongoDao = MainMongoDao.getInstance();
        }

        @Override
        @SuppressWarnings("all")
        public void destroy(){
            super.destroy();
            mongoDao.close();
        }

    }


}
