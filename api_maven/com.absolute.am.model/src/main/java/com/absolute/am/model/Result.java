/**
 * 
 */
package com.absolute.am.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.absolute.am.model.MetaData;

/**
 * The output of a named view.
 *
 */
@XmlRootElement
public class Result {
    private MetaData metaData;
    
    /**
	 * MetaData describing the result (number of rows, column titles/types, etc.).
	 * 
	 */
    public MetaData getMetaData() {
    	return metaData;
    }
    public void setMetaData(MetaData theMetaData) {
    	metaData = theMetaData;
    }

//    private ArrayList<Object> rows;
//    public void setRows(ArrayList<Object> rows) {
//		this.rows = rows;
//	}
//    public ArrayList<Object> getRows() {
//    	return rows;
//    }    
    
    private Object[] rows;
    public void setRows(Object[] rows) {
		this.rows = rows;
	}

    /**
	 * Rows
	 */
    public Object[] getRows() {
    	return rows;
    }    
}
