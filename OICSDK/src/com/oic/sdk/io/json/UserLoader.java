package com.oic.sdk.io.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.oic.sdk.data.UserData;
import com.oic.sdk.data.UserData.SavedPlace;
import com.oic.sdk.io.IoUtil;

public class UserLoader {
	public static UserData loadUser(){
		UserData userData = new UserData();
		BufferedReader br = null;
		try {
			File userFile = getUserFile();
			if(userFile == null){
				userData = new UserData();
				return userData;
			}
			br = new BufferedReader(new FileReader(userFile));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			Gson gson = new GsonBuilder().create();
			userData = gson.fromJson(sb.toString(), new TypeToken<UserData>() {}.getType());
			if (userData == null) {
				userData = new UserData();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		sortSavedPlace(userData);
		return userData;
	}
	
	public static void sortSavedPlace(UserData userData){
		Collections.sort(userData.savedPlace, new Comparator<SavedPlace>(){

			@Override
			public int compare(SavedPlace lhs, SavedPlace rhs) {
				return (lhs.timeSaved-rhs.timeSaved)>0?-1:1;
			}
			
		});
	}
	
	public static boolean saveUser(UserData user) throws IOException{
		Gson gson = new Gson();
		String json = gson.toJson(user);
		FileWriter out = null;
		try {
			File userFile = getUserFile();
			if(userFile == null){
				return false;
			}
			out = new FileWriter(userFile);
			out.write(json);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(out!=null){
				out.close();
			}
		}
		return true;
	}
	
	private static File getUserFile(){
		try{
			File folder = new File(IoUtil.getOicPath(),"user");
			if (!folder.isDirectory()) {
				folder.mkdirs();
			}
			return new File(folder.getAbsolutePath(), "user1.dat");
		}catch(Exception e){
			return null;
		}
	}
}
