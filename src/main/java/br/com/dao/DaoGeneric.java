package br.com.dao;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.jpautil.JPAUtil;

public class DaoGeneric<E> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void salvarEntidade(E entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		
		entityTransaction.begin();
		entityManager.merge(entidade);
		entityTransaction.commit();
		entityManager.close();
	}
	
	public E atualizarEntidade(E entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		E ent = entityManager.merge(entidade);
		transaction.commit();
		entityManager.close();
		
		return ent;
	}
	
	public E bucarEntidade(Class<E> ent, Long id) {
		EntityManager em = JPAUtil.getEntityManager();
		em.getTransaction().begin();
		
		E res = em.find(ent, id);
		
		em.getTransaction().commit();
		em.close();
		
		return res;
	}
	
	public void deletarEntidade(Class<E> entidade, Long idDeleta) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		
		transaction.begin();
		
		entityManager.createQuery("delete from "+ entidade.getName() +" where id="+ idDeleta)
		.executeUpdate();
		
		transaction.commit();
		entityManager.close();
	}
	
	public List<E> listarEntitdade(Class<E> entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		List<E> listEnt = entityManager.createQuery("from " + entidade.getName()).getResultList();
		
		transaction.commit();
		entityManager.close();
		return listEnt;
	}
	
}









