package com.kubrakocyigit.sharefreely_proje1;

public class BegeniPojo{
	private boolean tf;
	private String mesaj;

	public void setTf(boolean tf){
		this.tf = tf;
	}

	public boolean isTf(){
		return tf;
	}

	public void setMesaj(String mesaj){
		this.mesaj = mesaj;
	}

	public String getMesaj(){
		return mesaj;
	}

	@Override
 	public String toString(){
		return 
			"BegeniPojo{" + 
			"tf = '" + tf + '\'' + 
			",mesaj = '" + mesaj + '\'' +
			"}";
		}
}
