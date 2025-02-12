package com.jcrm1.minecraft.bacontweaks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileTextSource implements RandomTextSource {
	private static final String DEFAULT_NAMES_LIST = "Mr. Smith\n"
			+ "Martin\n"
			+ "Guy\n"
			+ "Anthony\n"
			+ "Akshat\n"
			+ "Alex\n"
			+ "Jonathan";
	private CircularStack<String> names;
	public FileTextSource(File namesFile) throws Exception {
		List<String> names = null;
		try {
			if (namesFile.createNewFile()) {
				FileWriter fw = new FileWriter(namesFile);
				fw.write(DEFAULT_NAMES_LIST);
				fw.close();
			}
			BufferedReader br = new BufferedReader(new FileReader(namesFile));
			names = br.lines().collect(Collectors.toList());
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (names == null) {
			throw new Exception("Refusing to populate name list as the file could not be read");
		}
		Collections.shuffle(names);
		this.names = new CircularStack<String>(names);
	}
	@Override
	public String getNext() {
		return names.pop();
	}
	
	class CircularStack<T> {
		private List<T> backingList;
		private int idx = 0;
		public CircularStack(List<T> backingList) {
			this.backingList = backingList;
		}
		public T pop() {
			idx++;
			if (idx >= backingList.size()) idx = 0;
			return backingList.get(idx);
		}
//		public void push(T item) {
//			backingList.add(idx + 1, item);
//			idx++;
//		}
	}
}
