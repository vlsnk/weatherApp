package my.weatherApp.service;

import my.weatherApp.dao.ClientDao;
import my.weatherApp.dao.ClientDaoImpl;
import my.weatherApp.model.Visitor;

public class VisitorServiceImpl implements VisitorService {

    private static VisitorServiceImpl instance;
    private ClientDao clientDao;

    private VisitorServiceImpl(){
        clientDao = ClientDaoImpl.getInstance();
    }

    public static VisitorServiceImpl getInstance(){
        if (instance == null) {
            instance = new VisitorServiceImpl();
        }
        return instance;
    }

    @Override
    public Visitor getInfo(String ip) {
        Visitor v = clientDao.getVisitor(ip);
        Visitor newVisitor;
        if (v == null) {
            newVisitor = new Visitor(ip, 1);
            clientDao.addVisitor(newVisitor);
        } else {
            int count = v.getCount();
            count++;
            newVisitor = new Visitor(ip,count);
            clientDao.update(newVisitor);
        }
        return newVisitor;
    }

}