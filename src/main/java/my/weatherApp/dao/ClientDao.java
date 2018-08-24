package my.weatherApp.dao;

import my.weatherApp.model.Visitor;

public interface ClientDao {

    boolean isReady();
    void addVisitor(Visitor visitor);
    Visitor getVisitor();

}
