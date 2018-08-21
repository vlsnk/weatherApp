package my.weatherApp.dao;

import my.weatherApp.model.Visitor;

public interface ClientDao {

    void addVisitor(Visitor visitor);
    Visitor getVisitor(String ip);
    void update(Visitor v);

}
