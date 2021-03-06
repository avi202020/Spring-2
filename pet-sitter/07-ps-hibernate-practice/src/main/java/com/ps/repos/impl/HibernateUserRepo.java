package com.ps.repos.impl;

import com.ps.ents.User;
import com.ps.repos.UserRepo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Set;

import static com.ps.ents.User.FIND_BY_USERNAME_EXACT;
import static com.ps.ents.User.FIND_BY_USERNAME_LIKE;

@Repository
@Transactional
public class HibernateUserRepo implements UserRepo {

	@Autowired
	private SessionFactory sessionFactory;

	public Session session() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void flush() {
		session().flush();
	}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<User> findAll() {
        System.out.println(TransactionSynchronizationManager.getCurrentTransactionName());
        return session().createQuery("from User u").list();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public User findById(Long id) {
        findAll();
        System.out.println(TransactionSynchronizationManager.getCurrentTransactionName());
        return session().get(User.class, id);
    }

	@Override
	public void save(User user) {
		session().persist(user);
	}

	@Override
	public List<User> findAllByUserName(String username, boolean exactMatch) {
		if (exactMatch) {
			return session().createNamedQuery(FIND_BY_USERNAME_EXACT)
					.setParameter("un", username).list();
		} else {
			return session().createQuery(FIND_BY_USERNAME_LIKE)
					.setParameter("un", "%" + username + "%").list();
		}
	}

	@Override
	public String findUsernameById(Long id) {
		return (String) session().createQuery("select u.username from User u where u.id= :id").
				setParameter("id", id).uniqueResult();
	}

	@Override
	public long countUsers() {
		return (Long) session().createQuery("select count(u) from User u").uniqueResult();
	}

	@Override
	public void updatePassword(Long userId, String newPass) {
		final User user = (User) session().createQuery("from User u where u.id= :id").
				setParameter("id", userId).uniqueResult();
		user.setPassword(newPass);
		session().update(user);
	}

	@Override
	public void updateUsername(Long userId, String username) {
		final User user = (User) session().createQuery("from User u where u.id= :id").
				setParameter("id", userId).uniqueResult();
		user.setUsername(username);
		session().update(user);
	}

	@Override
	public void deleteById(Long userId) {
		final User user = (User) session().createQuery("from User u where u.id= :id").
				setParameter("id", userId).uniqueResult();
		session().delete(user);
	}

	@Override
	public void save(Set<User> users) {
		for (User user : users) {
			session().saveOrUpdate(user);
		}
	}
}
