package introsde.document.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import introsde.document.dao.LifeCoachDao;
@Entity
@Table(name="\"Measure\"") 

@NamedQuery(name="Measure.findAll", query="SELECT p FROM Measure p")
public class Measure implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator="sqlite_Measure")
    @TableGenerator(name="sqlite_Measure", table="sqlite_sequence",
        pkColumnName="name", valueColumnName="seq",
        pkColumnValue="Measure")
    @Column(name="\"idMeasure\"") 
    private int idMeasure;
    
    @ManyToOne
	@JoinColumn(name="\"idPerson\"",referencedColumnName="\"idPerson\"")
    private Person person;
    
    @Column(name="\"value\"")
    private String value;
    
    @Column(name="\"valueType\"")
    private String valueType;
    
    @Column(name="\"type\"")
    private String type;
    
    @Temporal(TemporalType.DATE)
    @Column(name="\"dateRegistered\"")
    private Date date; 
    
    public int getIdMeasure() {
		return idMeasure;
	}

	public void setIdMeasure(int idMeasure) {
		this.idMeasure = idMeasure;
	}


	@XmlTransient
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	
    public String getDate(){
    	if(this.date == null) {
    	      return null;
    	}
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(this.date);
    }
    
    @XmlTransient
    public Date getDateRegistered(){
    	return this.date;
    }

    public void setDate(String date) throws ParseException{
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date d = format.parse(date);
        this.date = d;
    }
	
	public static Measure getMeasureById(int mid) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        Measure p = em.find(Measure.class, mid);
        LifeCoachDao.instance.closeConnections(em);
        return p;
    }

    public static List<Measure> getAll() {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        List<Measure> list = em.createNamedQuery("Measure.findAll", Measure.class)
            .getResultList();
        LifeCoachDao.instance.closeConnections(em);
        return list;
    }

    public static Measure saveMeasure(int personId, Measure ls) throws ParseException {
    	ls.setPerson(Person.getPersonById(personId));
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    	ls.setDate(sdf.format(new Date()));
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(ls);
        tx.commit();
        LifeCoachDao.instance.closeConnections(em);
        Person.refreshLists(personId);
        return ls;
    } 

    public static Measure updateMeasure(Measure ls) {
    	if(ls.getPerson() == null){
    		ls.setPerson(Measure.getMeasureById(ls.getIdMeasure()).getPerson());
    	}
        EntityManager em = LifeCoachDao.instance.createEntityManager(); 
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        ls=em.merge(ls);
        tx.commit();
        LifeCoachDao.instance.closeConnections(em);
        return ls;
    }

    public static void removeMeasure(Measure ls) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        ls=em.merge(ls);
        em.remove(ls);
        tx.commit();
        LifeCoachDao.instance.closeConnections(em);
    }
    
    public static List<Measure> getMeasuresByType(int id, String type){
    	System.out.println("Person with id: "+id+ "and type:"+type);
    	EntityManager em = LifeCoachDao.instance.createEntityManager();
    	List<Measure> ls = em
    			.createQuery("SELECT m FROM Measure m WHERE m.type = :type and m.person.idPerson = :id", Measure.class)
   				.setParameter("type", type)
   				.setParameter("id", id).getResultList();
   	    LifeCoachDao.instance.closeConnections(em);

   	    if (ls.isEmpty()){
   	    	return null;
    	}
        return ls;
    	
    }

}