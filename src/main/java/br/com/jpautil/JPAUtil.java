package br.com.jpautil;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.EntityManager;

public class JPAUtil {
	
	private static EntityManagerFactory factory = null;
	
	static {
		if (factory == null) {
			factory = Persistence.createEntityManagerFactory("jpaHibernateJsf");
		}
	}
	
	public static EntityManager getEntityManager() {
		return factory.createEntityManager();
	}
	
}