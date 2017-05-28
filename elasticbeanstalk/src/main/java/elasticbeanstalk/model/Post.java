package elasticbeanstalk.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="content_table")
public class Post implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String tittle;
	private String cont;
	private String url;
	
	public Post(){
	
	}
	
	public Post(String tittle, String cont, String url)
	{
		super();
		this.tittle = tittle;
		this.cont = cont;
		this.url = url;		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTittle() {
		return tittle;
	}

	public void setTittle(String tittle) {
		this.tittle = tittle;
	}

	public String getCont() {
		return cont;
	}

	public void setCont(String cont) {
		this.cont = cont;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		return "Post [id=" + getId() + ", tittle=" + tittle + ", cont=" + cont + ", url=" + url + "]";
	}
}
