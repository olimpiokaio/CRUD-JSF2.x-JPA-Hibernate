package br.com.repository;

import br.com.model.Estados;
import br.com.model.Usuario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.com.jpautil.JPAUtil;

public class IDaoUsuarioImpl implements IDaoUsuario, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Usuario consultarUsuario(String login, String senha) {
		EntityManager em = JPAUtil.getEntityManager();
		em.getTransaction().begin();
		
		String jpql = "select u from Usuario u where u.login = :login and u.senha = :senha";
		Query query = em.createQuery(jpql);
		query.setParameter("login", login);
		query.setParameter("senha", senha);
		
		Usuario user = (Usuario)query.getSingleResult();
		
		em.getTransaction().commit();
		em.close();
		return user;
	}

	@Override
	public List<SelectItem> listaEstados() {
		
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		
		EntityManager em = JPAUtil.getEntityManager();
		em.getTransaction().begin();
		
		List<Estados> estados = em.createQuery("from Estados").getResultList();
		
		for (Estados estado : estados) {
			selectItems.add(new SelectItem(estado.getId(), estado.getNome()));
		}
		
		return selectItems;
	}
	
	public List<Estados> getListaEstados() {
		EntityManager em = JPAUtil.getEntityManager();
		em.getTransaction().begin();
		
		TypedQuery<Estados> tquery = em.createQuery("select e from Estados e", Estados.class);
		List<Estados> estados = tquery.getResultList();
		
		em.getTransaction().commit();
		em.close();
		
		return estados;
	}
	
}
