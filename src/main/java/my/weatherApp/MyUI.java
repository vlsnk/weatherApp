package my.weatherApp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import my.weatherApp.dao.ClientDaoImpl;
import my.weatherApp.dao.CurrencyDaoImpl;
import my.weatherApp.dao.WeatherDaoImpl;

import java.io.InputStream;
import java.util.Properties;


@Theme("mytheme")
public class MyUI extends UI {

    private DashboardForm dashboardForm = new DashboardForm();
    private String mytheme = "mytheme";

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        this.setPrimaryStyleName(mytheme);
        final VerticalLayout layout = new VerticalLayout();
        dashboardForm.getDashBoard(vaadinRequest.getRemoteAddr());
        layout.addComponents(dashboardForm);
        layout.setComponentAlignment(dashboardForm, Alignment.MIDDLE_CENTER);
        setContent(layout);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = true)
    public static class MyUIServlet extends VaadinServlet {

        private final static String DB = "mongodb://localhost:27017";
        private final static String DB_NAME = "weatherApp";
        private MongoClient mongoClient;
        Properties p = new Properties();

        @Override
        public void init() throws ServletException {
            mongoClient = new MongoClient(new MongoClientURI(DB));
            MongoDatabase database = mongoClient.getDatabase(DB_NAME);
            initDao(database);
        }

        static void initDao(MongoDatabase db){
            ClientDaoImpl.setDB(db);
            CurrencyDaoImpl.setDB(db);
            WeatherDaoImpl.setDB(db);
        }

        @Override
        @SuppressWarnings("all")
        public void destroy(){
            super.destroy();
            mongoClient.close();
        }

    }


}
