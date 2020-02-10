package br.com.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import br.com.jpautil.JPAUtil;
import br.com.model.Lancamento;
import br.com.model.Usuario;

public class IDaoUsuairoLancamentos implements IDaoLancamentos {

	@Override
	public List<Lancamento> getLancamentos(Usuario userSession) {
		EntityManager em = new JPAUtil().getEntityManager();
		em.getTransaction().begin();
		
		String jpql = "select l from Lancamento l where l.usuario = :user"
				+ " order by l.id";
		
		TypedQuery<Lancamento> query = em.createQuery(jpql, Lancamento.class);
		query.setParameter("user", userSession);
		
		List<Lancamento> lista = (List<Lancamento>) query.getResultList();
		
		em.getTransaction().commit();
		em.close();
		return lista;
	}
	
}
