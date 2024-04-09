package pn.Model;

import org.openstreetmap.gui.jmapviewer.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Define uma região e métodos de acesso e verificação.
 * <p>
 * A região pode ser concava ou convexa
 * 
 * @author meslin
 *
 */
public class Region
{
	/** Region number */
	private int number;
	/** Region filename */
	private String filename;
	/** Region coordinates (latitude longitude) */
	private List<Coordinate> points;

	/**
	 * Constroi uma região vazia
	 */
	public Region()
	{
		super();
		points = new ArrayList<Coordinate>();
	}
	
	/**
	 * 
	 * @param number
	 */
	public void setNumber(int number) { this.number = number; }
	
	/**
	 * 
	 * @return
	 */
	public int getNumber() { return this.number; }

	/**
	 * Adiciona um ponto à região
	 * 
	 * @param point
	 */
	public void add(Coordinate point)
	{
		this.points.add(point);
	}
	public List<Coordinate> getPoints()
	{
		return this.points;
	}
	
	/**
	 * Verifica se o ponto pertence a região
	 * 
	 * @param coordinates
	 * @return verdadeiro se o ponto pertencer à região
	 */
	public boolean contains(Coordinate coordinates)
	{
		boolean result = false;
		
		/*
		 * para todo segmento de reta da região cuja reta cruza a linha y do ponto,
		 * se a soma dos pontos x menores do que a posição x do ponto for ímpar,
		 * o ponto estará dentro da região
		 */
		for(int i=0, j=this.points.size()-1; i<this.points.size(); j=i++)
		{
			if(((this.points.get(i).getLat() > coordinates.getLat()) != (this.points.get(j).getLat() > coordinates.getLat()))
			&& (coordinates.getLon() < ((this.points.get(j).getLon()-this.points.get(i).getLon()) * (coordinates.getLat()-this.points.get(i).getLat()) / (this.points.get(j).getLat()-this.points.get(i).getLat()) + this.points.get(i).getLon())))
				result = !result;
		}
		return result;
	}

	public boolean contains(MobileObject coordinates)
	{
		return contains(new Coordinate(coordinates.getLatitude(), coordinates.getLongitude()));
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(List<Coordinate> points) {
		this.points = points;
	}
}
