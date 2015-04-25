package jcocosketch.nodebridge;

import java.util.ArrayList;

import jcocosketch.Line;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class MongoControl{
	MongoClient client;
	DB userDB;
	DB dataDB;
	
	String currentUser;
	
	boolean saveUserLines;
	boolean saveCompLines;
	boolean saveModes;
	boolean saveFreq;
	JSONSerializer serializer;
	JSONDeserializer deserializer;
	
	public MongoControl(){
		currentUser = "testUser";
		connect();
		serializer = new JSONSerializer();
		deserializer = new JSONDeserializer();
		
	}
	public MongoControl(String currentUser){
		this.currentUser = currentUser;
		connect();
		serializer = new JSONSerializer();
		deserializer = new JSONDeserializer();
	}

	/**
	 * connect to mongo
	 */
	private void connect() {
		try{
			client = new MongoClient( "localhost" , 27017 );
	        userDB = client.getDB("dappUsers");
	        dataDB = client.getDB("dappData");
	        System.out.println("Connected to mongo");
	        DBCollection col = dataDB.getCollection(currentUser);
	        System.out.println("Connected to mongo!");
	        System.out.println("Current user: " + currentUser);
		}catch(Exception e){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		saveUserLines = true;
		saveCompLines = true;
		saveModes = true;
		saveFreq = true;
	}
	/**
	 * retrieve user from db
	 * @param userID
	 * @return BDBObj of user info
	 */
	private BasicDBObject user(String userID){
		BasicDBObject obj = new BasicDBObject().append("userID",userID);
		DBCollection col = userDB.getCollection("users");
		DBCursor cursor = col.find(obj);
		if(!cursor.hasNext()){
			return null;
		}else{
			return (BasicDBObject) cursor.next();
		}
	}

	/**
	 * gets all line data from db
	 * @param userID
	 * @return userlines, complines, freq, modes
	 */
	
	public BasicDBObject[] data(){
		DBCollection col;
		
		try{
			col = dataDB.getCollection(currentUser).getCollection("lineData");
			BasicDBObject[] data = new BasicDBObject[4];
			DBCursor cursor = col.find();
			int i = 0;
			while(cursor.hasNext()){
				data[i] = (BasicDBObject) cursor.next();
				i++;
			}
			return data;
		}
		catch(Exception e){
			System.out.println("Can't find collection");
		}
		return null;
	}
	
	private void toggleSave(boolean userLines, boolean compLines, boolean modes, boolean freq){
		saveUserLines = userLines;
		saveCompLines = compLines;
		saveModes = modes;
		saveFreq = freq;
	}
	/**
	 * converts string to json formatted db object
	 * @param coll collection to insert into
	 * @param data stringified json
	 * @return the object created
	 */
	private BasicDBObject convertDoc(String coll, String data) {
		BasicDBObject obj = null;
		switch (coll) {
		case "userLines":
			obj = (BasicDBObject) JSON.parse(data.substring(1,data.length()));
			obj.append("_id","userLines");
			break;
			
		case "compLines":
			obj = (BasicDBObject) JSON.parse(data.substring(1,data.length()));
			obj.append("_id","compLines");
			break;
		
		case "allModes":
			obj = (BasicDBObject) JSON.parse("{" + data + "}");
			obj.append("_id","allModes");
			break;
		
		case "freq":
			obj = (BasicDBObject) JSON.parse(data);
			obj.append("_id","freq");
			break;
		default:
			obj = null;			
		}
		return obj;
	}
	
	/**
	 * creates a user object for mongo
	 * @param id
	 * @param name
	 * @param age
	 * @param date
	 * @return
	 */
	public void createUser(String id, String name){
		//add new parameters as needed
		BasicDBObject obj = new BasicDBObject().append("name", name)
				.append("userID", id);
		DBCollection col = client.getDB("dappUsers").getCollection("users");
		BasicDBObject query = new BasicDBObject().append("userID", id);
		if(col.distinct("name").contains(name)){
			col.update(query, obj);
		}else{
			col.insert(obj);
		}
	}
	
	public void saveLine(ArrayList<Line> lines){
		String serializedLines = serializer.deepSerialize(lines);
		DBCollection userCol = dataDB.getCollection(currentUser);
		DBCollection lineCol = userCol.getCollection("lineData");
		//choose which to save
		toggleSave(true,true,false,true);
		//update or insert document
		BasicDBObject query = new BasicDBObject().append("_id", "userLines");
		if(saveUserLines){
			if(lineCol.distinct("_id").contains("userLines")){
				lineCol.update(query, convertDoc("userLines",serializedLines));
			}else{
				lineCol.insert(convertDoc("userLines",serializedLines));
			}
		}
	}
	
	//debugging functions
	private void printCollection(){
		DBCursor cursor = client.getDB("dappData").getCollection(currentUser).getCollection("lineData").find();
		while(cursor.hasNext()){
			System.out.println(cursor.next());
		}
	}
	public void printData(BasicDBObject[] data){
		for(BasicDBObject o: data){
			System.out.println(o);
		}
	}
	
	public String linesString(boolean user){
		Line line = (Line)deserializer.deserialize(data()[1].toString());
		if(user){
			if(!line.compGenerated){
				return data()[1].toString();
			} else {
				return data()[2].toString();
			}
		} else {
			if(line.compGenerated){
				return data()[1].toString();
			} else {
				return data()[2].toString();
			}
		}
	}
	
	public void setCurrentUser(String currentUser){
		this.currentUser = currentUser;
	}
	
	private void printUsers(){
		DBCursor cursor = client.getDB("dappUsers").getCollection("users").find();
		while(cursor.hasNext()){
			System.out.println(cursor.next());
		}
	}

}