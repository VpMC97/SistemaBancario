/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CONEXION.bd;

import gt.edu.umg.bd.exceptions.NonexistentEntityException;
import gt.edu.umg.bd.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Isa
 */
public class TipoCuentaJpaController implements Serializable {

    public TipoCuentaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TipoCuenta tipoCuenta) throws PreexistingEntityException, Exception {
        if (tipoCuenta.getCuentaList() == null) {
            tipoCuenta.setCuentaList(new ArrayList<Cuenta>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Cuenta> attachedCuentaList = new ArrayList<Cuenta>();
            for (Cuenta cuentaListCuentaToAttach : tipoCuenta.getCuentaList()) {
                cuentaListCuentaToAttach = em.getReference(cuentaListCuentaToAttach.getClass(), cuentaListCuentaToAttach.getIdCuenta());
                attachedCuentaList.add(cuentaListCuentaToAttach);
            }
            tipoCuenta.setCuentaList(attachedCuentaList);
            em.persist(tipoCuenta);
            for (Cuenta cuentaListCuenta : tipoCuenta.getCuentaList()) {
                TipoCuenta oldIdTipoCuentaOfCuentaListCuenta = cuentaListCuenta.getIdTipoCuenta();
                cuentaListCuenta.setIdTipoCuenta(tipoCuenta);
                cuentaListCuenta = em.merge(cuentaListCuenta);
                if (oldIdTipoCuentaOfCuentaListCuenta != null) {
                    oldIdTipoCuentaOfCuentaListCuenta.getCuentaList().remove(cuentaListCuenta);
                    oldIdTipoCuentaOfCuentaListCuenta = em.merge(oldIdTipoCuentaOfCuentaListCuenta);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTipoCuenta(tipoCuenta.getIdTipoCuenta()) != null) {
                throw new PreexistingEntityException("TipoCuenta " + tipoCuenta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TipoCuenta tipoCuenta) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoCuenta persistentTipoCuenta = em.find(TipoCuenta.class, tipoCuenta.getIdTipoCuenta());
            List<Cuenta> cuentaListOld = persistentTipoCuenta.getCuentaList();
            List<Cuenta> cuentaListNew = tipoCuenta.getCuentaList();
            List<Cuenta> attachedCuentaListNew = new ArrayList<Cuenta>();
            for (Cuenta cuentaListNewCuentaToAttach : cuentaListNew) {
                cuentaListNewCuentaToAttach = em.getReference(cuentaListNewCuentaToAttach.getClass(), cuentaListNewCuentaToAttach.getIdCuenta());
                attachedCuentaListNew.add(cuentaListNewCuentaToAttach);
            }
            cuentaListNew = attachedCuentaListNew;
            tipoCuenta.setCuentaList(cuentaListNew);
            tipoCuenta = em.merge(tipoCuenta);
            for (Cuenta cuentaListOldCuenta : cuentaListOld) {
                if (!cuentaListNew.contains(cuentaListOldCuenta)) {
                    cuentaListOldCuenta.setIdTipoCuenta(null);
                    cuentaListOldCuenta = em.merge(cuentaListOldCuenta);
                }
            }
            for (Cuenta cuentaListNewCuenta : cuentaListNew) {
                if (!cuentaListOld.contains(cuentaListNewCuenta)) {
                    TipoCuenta oldIdTipoCuentaOfCuentaListNewCuenta = cuentaListNewCuenta.getIdTipoCuenta();
                    cuentaListNewCuenta.setIdTipoCuenta(tipoCuenta);
                    cuentaListNewCuenta = em.merge(cuentaListNewCuenta);
                    if (oldIdTipoCuentaOfCuentaListNewCuenta != null && !oldIdTipoCuentaOfCuentaListNewCuenta.equals(tipoCuenta)) {
                        oldIdTipoCuentaOfCuentaListNewCuenta.getCuentaList().remove(cuentaListNewCuenta);
                        oldIdTipoCuentaOfCuentaListNewCuenta = em.merge(oldIdTipoCuentaOfCuentaListNewCuenta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tipoCuenta.getIdTipoCuenta();
                if (findTipoCuenta(id) == null) {
                    throw new NonexistentEntityException("The tipoCuenta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoCuenta tipoCuenta;
            try {
                tipoCuenta = em.getReference(TipoCuenta.class, id);
                tipoCuenta.getIdTipoCuenta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipoCuenta with id " + id + " no longer exists.", enfe);
            }
            List<Cuenta> cuentaList = tipoCuenta.getCuentaList();
            for (Cuenta cuentaListCuenta : cuentaList) {
                cuentaListCuenta.setIdTipoCuenta(null);
                cuentaListCuenta = em.merge(cuentaListCuenta);
            }
            em.remove(tipoCuenta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TipoCuenta> findTipoCuentaEntities() {
        return findTipoCuentaEntities(true, -1, -1);
    }

    public List<TipoCuenta> findTipoCuentaEntities(int maxResults, int firstResult) {
        return findTipoCuentaEntities(false, maxResults, firstResult);
    }

    private List<TipoCuenta> findTipoCuentaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TipoCuenta.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public TipoCuenta findTipoCuenta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TipoCuenta.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipoCuentaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TipoCuenta> rt = cq.from(TipoCuenta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
