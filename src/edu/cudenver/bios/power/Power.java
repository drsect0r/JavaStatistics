/*
 * Java Statistics.  A java library providing power/sample size estimation for 
 * the general linear model.
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.cudenver.bios.power;

/**
 * Abstract base class for power calculation results.
 * 
 * @author Sarah Kreidler
 *
 */
public abstract class Power
{	
	String errMsg = null;
	
	double nominalPower;
	
	double actualPower; 
	
	int totalSampleSize;
	
	double alpha;
	
	/**
	 * Create a power result object.
	 * 
	 * @param nominalPower target power (if performing sample size, effect size)
	 * @param actualPower calculated power
	 * @param totalSampleSize total sample size for this power result
	 * @param alpha type 1 error for this power result
	 */
	public Power(double nominalPower, double actualPower, int totalSampleSize, double alpha)
	{
		this.nominalPower = nominalPower;
		this.actualPower = actualPower;
		this.totalSampleSize = totalSampleSize;
		this.alpha = alpha;
	}
	
	/**
	 * Create an XML representation of the power result
	 * @return XML representation of the power result
	 */
	public abstract String toXML();
	
	/**
	 * Get the nominal power value
	 * @return nominal power
	 */
	public double getNominalPower()
	{
		return nominalPower;
	}

	/**
	 * Set the target or nominal power value
	 * @param nominalPower
	 */
	public void setNominalPower(double nominalPower)
	{
		this.nominalPower = nominalPower;
	}

	/**
	 * Get the calculated power value
	 * @return calculated power
	 */
	public double getActualPower()
	{
		return actualPower;
	}

	/**
	 * Set the calculated power value
	 * @param actualPower
	 */
	public void setActualPower(double actualPower)
	{
		this.actualPower = actualPower;
	}

	/**
	 * Get the total sample size
	 * @return total N
	 */
	public int getTotalSampleSize()
	{
		return totalSampleSize;
	}

	/**
	 * Set the total sample size
	 * @param totalSampleSize
	 */
	public void setTotalSampleSize(int totalSampleSize)
	{
		this.totalSampleSize = totalSampleSize;
	}

	/**
	 * Get the type I error value
	 * @return alpha
	 */
	public double getAlpha()
	{
		return alpha;
	}

	/**
	 * Set the type I error value
	 * @param alpha
	 */
	public void setAlpha(double alpha)
	{
		this.alpha = alpha;
	}
	
	
}
