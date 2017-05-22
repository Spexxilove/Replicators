package engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses obj wavefront triangulated files with vertices, texture coordinates
 * and normals
 * @author thei
 */
public class OBJReader{
	private List<Float> attributes = new ArrayList<Float>();
	private int index = 0;
	private List<Float> v = null;
	private List<Float> vt = null;
	private List<Float> vn = null;
	private List<Integer> indices = new ArrayList<Integer>();
	/**
	 * @param filename as needed by {@link getResourceAsStream()}
	 */
	public OBJReader(String filename){
		parse(filename);
	}
	/**
	 * Parses obj file, calls methods depending on line beginning: v
	 * {@link #parseV(String[])} vt {@link #parseVT(String[])} vn
	 * {@link #parseVN(String[])} f {@link #parseF(Map, String[])}
	 * @param filename
	 */
	private void parse(String filename){
		v = new ArrayList<Float>();
		vt = new ArrayList<Float>();
		vn = new ArrayList<Float>();
		Map<String, Integer> f = new HashMap<String, Integer>();
		try{
			InputStream in = OBJReader.class.getClassLoader()
					.getResourceAsStream(filename);
			if(in == null)
				System.out.println(filename + " not found");
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					in));
			String line;
			while((line = br.readLine()) != null){
				if(line.contains("#"))
					line = line.substring(0, line.indexOf("#"));
				line = line.trim();
				if(line.length() > 0){
					String values[] = line.split(" ");
					switch(values[0]){
						case "v":
							parseV(values); // vertices
							break;
						case "vt":
							parseVT(values); // uv
							break;
						case "vn":
							parseVN(values); // normals
							break;
						case "f":
							parseF(f, values); // parse triangles
							break;
						default:
							break;
					}
				}
			}
		}
		catch(final IOException e){
			System.out.println("OBJReader: " + e);
		}
	}
	/**
	 * Stores vertices in a List
	 * @param values 3 floats
	 * @throws IOException
	 */
	private void parseV(String values[]) throws IOException{
		if(3 != values.length - 1)
			throw new IOException("#v");
		for(int i = 1; i <= 3; i++)
			v.add(Float.valueOf(values[i]));
	}
	/**
	 * Stores texture coordinates in a List
	 * @param values 2 floats
	 * @throws IOException
	 */
	private void parseVT(String values[]) throws IOException{
		if(2 != values.length - 1)
			throw new IOException("#vt");
		for(int i = 1; i <= 2; i++)
			vt.add(Float.valueOf(values[i]));
	}
	/**
	 * Stores normals in a List
	 * @param values 3 floats
	 * @throws IOException
	 */
	private void parseVN(String values[]) throws IOException{
		if(3 != values.length - 1)
			throw new IOException("#vn");
		for(int i = 1; i <= 3; i++)
			vn.add(Float.valueOf(values[i]));
	}
	/**
	 * Stores indices for triangle rendering in in a List, checks if a matching
	 * combination of vtn exists and uses that index calls
	 * {@link #parseVTN(Map, String)}
	 * @param f
	 * @param values
	 * @throws IOException
	 */
	private void parseF(Map<String, Integer> f, String values[])
			throws IOException{
		if(3 != values.length - 1)
			throw new IOException("#f");
		for(int i = 1; i <= 3; i++){ // for every vtn
			if(!f.containsKey(values[i])) // if no such vtn in list
				parseVTN(f, values[i]); // add vtn
			indices.add(f.get(values[i])); // add index
		}
	}
	/**
	 * Stores vtn's in attributes list
	 * @param f
	 * @param value
	 * @throws IOException
	 */
	private void parseVTN(Map<String, Integer> f, String value)
			throws IOException{
		String[] values = value.split("/");
		if(3 != values.length)
			throw new IOException("vtn");
		for(int i = 0; i < values.length; ++i){
			if(values[i].isEmpty()){
				values[i] = "1";
			}
		}
		int vID = Integer.valueOf(values[0]) - 1;
		for(int i = 0; i < 3; i++)
			// add x,y,z
			attributes.add(v.get(vID * 3 + i));
		int vtID = Integer.valueOf(values[1]) - 1;
		for(int i = 0; i < 2; i++)
			// add u,v
			attributes.add(vt.get(vtID * 2 + i));
		int vnID = Integer.valueOf(values[2]) - 1;
		for(int i = 0; i < 3; i++)
			// add nx,ny,nz
			attributes.add(vn.get(vnID * 3 + i));
		f.put(value, index++); // add vtn to list
	}
	public List<Integer> getIndices(){
		return indices;
	}
	public List<Float> getAttributes(){
		return attributes;
	}
}
