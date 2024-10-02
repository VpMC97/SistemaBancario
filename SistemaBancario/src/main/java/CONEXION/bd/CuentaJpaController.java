/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CONEXION.bd;

import gt.edu.umg.bd.exceptions.NonexistentEntityException;
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
public class CuentaJpaController implements Serializable {

    public CuentaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cuenta cuenta) {
        if (cuenta.getTransaccionList() == null) {
            cuenta.setTransaccionList(new ArrayList<Transaccion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente nitCliente = cuenta.getNitCliente();
            if (nitCliente != null) {
                nitCliente = em.getReference(nitCliente.getClass(), nitCliente.getNitCliente());
                cuenta.setNitCliente(nitCliente);
            }
            TipoCuenta idTipoCuenta = cuenta.getIdTipoCuenta();
            if (idTipoCuenta != null) {
                idTipoCuenta = em.getReference(idTipoCuenta.getClass(), idTipoCuenta.getIdTipoCuenta());
                cuenta.setIdTipoCuenta(idTipoCuenta);
            }
            List<Transaccion> attachedTransaccionList = new ArrayList<Transaccion>();
            for (Transaccion transaccionListTransaccionToAttach : cuenta.getTransaccionList()) {
                transaccionListTransaccionToAttach = em.getReference(transaccionListTransaccionToAttach.getClass(), transaccionListTransaccionToAttach.getIdTransaccion());
                attachedTransaccionList.add(transaccionListTransaccionToAttach);
            }
            cuenta.setTransaccionList(attachedTransaccionList);
            em.persist(cuenta);
            if (nitCliente != null) {
                nitCliente.getCuentaList().add(cuenta);
                nitCliente = em.merge(nitCliente);
            }
            if (idTipoCuenta != null) {
                idTipoCuenta.getCuentaList().add(cuenta);
                idTipoCuenta = em.merge(idTipoCuenta);
            }
            for (Transaccion transaccionListTransaccion : cuenta.getTransaccionList()) {
                Cuenta oldIdCuentaOfTransaccionListTransaccion = transaccionListTransaccion.getIdCuenta();
                transaccionListTransaccion.setIdCuenta(cuenta);
                transaccionListTransaccion = em.merge(transaccionListTransaccion);
                if (oldIdCuentaOfTransaccionListTransaccion != null) {
                    oldIdCuentaOfTransaccionListTransaccion.getTransaccionList().remove(transaccionListTransaccion);
                    oldIdCuentaOfTransaccionListTransaccion = em.merge(oldIdCuentaOfTransaccionListTransaccion);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cuenta cuenta) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cuenta persistentCuenta = em.find(Cuenta.class, cuenta.getIdCuenta());
            Cliente nitClienteOld = persistentCuenta.getNitCliente();
            Cliente nitClienteNew = cuenta.getNitCliente();
            TipoCuenta idTipoCuentaOld = persistentCuenta.getIdTipoCuenta();
            TipoCuenta idTipoCuentaNew = cuenta.getIdTipoCuenta();
            List<Transaccion> transaccionListOld = persistentCuenta.getTransaccionList();
            List<Transaccion> transaccionListNew = cuenta.getTransaccionList();
            if (nitClienteNew != null) {
                nitClienteNew = em.getReference(nitClienteNew.getClass(), nitClienteNew.getNitCliente());
                cuenta.setNitCliente(nitClienteNew);
            }
            if (idTipoCuentaNew != null) {
                idTipoCuentaNew = em.getReference(idTipoCuentaNew.getClass(), idTipoCuentaNew.getIdTipoCuenta());
                cuenta.setIdTipoCuenta(idTipoCuentaNew);
            }
            List<Transaccion> attachedTransaccionListNew = new ArrayList<Transaccion>();
            for (Transaccion transaccionListNewTransaccionToAttach : transaccionListNew) {
                transaccionListNewTransaccionToAttach = em.getReference(transaccionListNewTransaccionToAttach.getClass(), transaccionListNewTransaccionToAttach.getIdTransaccion());
                attachedTransaccionListNew.add(transaccionListNewTransaccionToAttach);
            }
            transaccionListNew = attachedTransaccionListNew;
            cuenta.setTransaccionList(transaccionListNew);
            cuenta = em.merge(cuenta);
            if (nitClienteOld != null && !nitClienteOld.equals(nitClienteNew)) {
                nitClienteOld.getCuentaList().remove(cuenta);
                nitClienteOld = em.merge(nitClienteOld);
            }
            if (nitClienteNew != null && !nitClienteNew.equals(nitClienteOld)) {
                nitClienteNew.getCuentaList().add(cuenta);
                nitClienteNew = em.merge(nitClienteNew);
            }
            if (idTipoCuentaOld != null && !idTipoCuentaOld.equals(idTipoCuentaNew)) {
                idTipoCuentaOld.getCuentaList().remove(cuenta);
                idTipoCuentaOld = em.merge(idTipoCuentaOld);
            }
            if (idTipoCuentaNew != null && !idTipoCuentaNew.equals(idTipoCuentaOld)) {
                idTipoCuentaNew.getCuentaList().add(cuenta);
                idTipoCuentaNew = em.merge(idTipoCuentaNew);
            }
            for (Transaccion transaccionListOldTransaccion : transaccionListOld) {
                if (!transaccionListNew.contains(transaccionListOldTransaccion)) {
                    transaccionListOldTransaccion.setIdCuenta(null);
                    transaccionListOldTransaccion = em.merge(transaccionListOldTransaccion);
                }
            }
            for (Transaccion transaccionListNewTransaccion : transaccionListNew) {
                if (!transaccionListOld.contains(transaccionListNewTransaccion)) {
                    Cuenta oldIdCuentaOfTransaccionListNewTransaccion = transaccionListNewTransaccion.getIdCuenta();
                    transaccionListNewTransaccion.setIdCuenta(cuenta);
                    transaccionListNewTransaccion = em.merge(transaccionListNewTransaccion);
                    if (oldIdCuentaOfTransaccionListNewTransaccion != null && !oldIdCuentaOfTransaccionListNewTransaccion.equals(cuenta)) {
                        oldIdCuentaOfTransaccionListNewTransaccion.getTransaccionList().remove(transaccionListNewTransaccion);
                        oldIdCuentaOfTransaccionListNewTransaccion = em.merge(oldIdCuentaOfTransaccionListNewTransaccion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cuenta.getIdCuenta();
                if (findCuenta(id) == null) {
                    throw new NonexistentEntityException("The cuenta with id " + id + " no longer exists.");
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
            Cuenta cuenta;
            try {
                cuenta = em.getReference(Cuenta.class, id);
                cuenta.getIdCuenta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cuenta with id " + id + " no longer exists.", enfe);
            }
            Cliente nitCliente = cuenta.getNitCliente();
            if (nitCliente != null) {
                nitCliente.getCuentaList().remove(cuenta);
                nitCliente = em.merge(nitCliente);
            }
            TipoCuenta idTipoCuenta = cuenta.getIdTipoCuenta();
            if (idTipoCuenta != null) {
                idTipoCuenta.getCuentaList().remove(cuenta);
                idTipoCuenta = em.merge(idTipoCuenta);
            }
            List<Transaccion> transaccionList = cuenta.getTransaccionList();
            for (Transaccion transaccionListTransaccion : transaccionList) {
                transaccionListTransaccion.setIdCuenta(null);
                transaccionListTransaccion = em.merge(transaccionListTransaccion);
            }
            em.remove(cuenta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cuenta> findCuentaEntities() {
        return findCuentaEntities(true, -1, -1);
    }

    public List<Cuenta> findCuentaEntities(int maxResults, int firstResult) {
        return findCuentaEntities(false, maxResults, firstResult);
    }

    private List<Cuenta> findCuentaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cuenta.class));
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

    public Cuenta findCuenta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cuenta.class, id);
        } finally {
            em.close();
        }
    }

    public int getCuentaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cuenta> rt = cq.from(Cuenta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
