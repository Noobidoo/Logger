package net.CraftRepo.Overwatch;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;

/**
 * CraftRepo Overwatch for Bukkit
 * @author AllGamer
 * 
 * Copyright 2011 AllGamer, LLC.
 * See LICENSE for licensing information.
 */

@SuppressWarnings("unused")
public class MySQLConnection 
{
	private Overwatchmain plugin;
	private File folder;
	private final static Logger log = Logger.getLogger("Minecraft");
	private static String logPrefix;
	static Object mysqldb = Overwatchmain.config.getProperty("mysqldb");
	static Object mysqluser = Overwatchmain.config.getProperty("mysqluser");
	static Object mysqlpass = Overwatchmain.config.getProperty("mysqlpass");

	@SuppressWarnings("static-access")
	public MySQLConnection()
	{
		this.logPrefix = Overwatchmain.logPrefix;
	}

	private final static String PLAYER_TABLE     = "CREATE TABLE `player_bans` "
		+ "("
		+ "`id`       	INT PRIMARY KEY, "
		+ "`name`     	VARCHAR(32) NOT NULL DEFAULT 'Player', "
		+ ")";

	private final static String IP_TABLE    = "CREATE TABLE `ip_bans` "
		+ "("
		+ "`id`       	INT PRIMARY KEY, "
		+ "'ip1'        INT NOT NULL DEFAULT '0', "
		+ "'ip2'        INT NOT NULL DEFAULT '0', "
		+ "'ip3'        INT NOT NULL DEFAULT '0', "
		+ "'ip4'        INT NOT NULL DEFAULT '0', "
		+ ")";

	private final static String EXEMPT_TABLE = "CREATE TABLE `exempt` "
		+ "("
		+ "`id`         INT PRIMARY KEY, "
		+ "`name`     	VARCHAR(32) NOT NULL DEFAULT 'Player', "
		+ ")";

	public boolean initialize() 
	{
		Logger log = Logger.getLogger("Minecraft");
		log.info(logPrefix + " Loading MySQL");
		Overwatchmain.config.load();

		if (!tableExists("player_bans")) 
		{
			log.info(logPrefix + " 'player_bans' table doesn't exist, creating...");
			if (!createTable(PLAYER_TABLE)) 
			{
				log.info(logPrefix + " Cannot make table 'player_bans', disabling plugin.");
				return false;
			}
		}

		if (!tableExists("ip_bans")) 
		{
			log.info(logPrefix + " 'ip_bans' table doesn't exist, creating...");
			if (!createTable(IP_TABLE)) 
			{
				log.info(logPrefix + " Cannot make table 'ip_bans', disabling plugin.");
				return false;
			}
		}

		if (!tableExists("exempt")) 
		{
			log.info(logPrefix + " 'exempt' table doesn't exist, creating now.");
			if (!createTable(EXEMPT_TABLE)) 
			{
				log.info(logPrefix + " Cannot make table 'exempt', disabling plugin.");
				return false;
			}
		}
		return true;
	}

	public static boolean sql(String sql) 
	{
		Connection conn = null;
		Statement st = null;
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(mysqldb.toString(),mysqluser.toString(),mysqlpass.toString());
			st = conn.createStatement();
			st.executeUpdate(sql);
			return true;
		}
		catch (SQLException e) 
		{
			return false;
		}
		catch (ClassNotFoundException e) 
		{
			log.info(logPrefix + " Error loading com.mysql.jdbc.Driver");
			return false;
		}
		finally 
		{
			try 
			{
				if (conn != null) conn.close();
				if (st != null) st.close();
			}
			catch (SQLException e) 
			{
				log.info(logPrefix + " Could not close DB Connections.");
				return false;
			}
		}
	}

	private static boolean tableExists(String table) 
	{
		Connection conn = null;
		ResultSet rs = null;
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(mysqldb.toString(),mysqluser.toString(),mysqlpass.toString());
			DatabaseMetaData dbm = conn.getMetaData();
			rs = dbm.getTables(null, null, table, null);
			if (!rs.next()) return false;
			return true;
		}
		catch (SQLException ex) 
		{
			log.info(logPrefix + " Table Check Exception");
			return false;
		}
		catch (ClassNotFoundException e) 
		{
			log.info(logPrefix + " Error loading com.mysql.jdbc.Driver");
			return false;
		}
		finally 
		{
			try 
			{
				if (rs != null) rs.close();
				if (conn != null) conn.close();
			}
			catch (SQLException ex) 
			{
				log.info(logPrefix + " Table Check SQL Exception (on closing)");
			}
		}
	}

	private static boolean createTable(String sql) 
	{
		Connection conn = null;
		Statement st = null;
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(mysqldb.toString(),mysqluser.toString(),mysqlpass.toString());
			st = conn.createStatement();
			st.executeUpdate(sql);
			return true;
		}
		catch (SQLException e) 
		{
			log.info(logPrefix + " Create Table Exception");
			return false;
		}
		catch (ClassNotFoundException e) 
		{
			log.info(logPrefix + " Error loading com.mysql.jdbc.Driver");
			return false;
		}
		finally 
		{
			try 
			{
				if (conn != null) conn.close();
				if (st != null) st.close();
			}
			catch (SQLException e) 
			{
				log.info(logPrefix + " Could not create the table (on close)");
				return false;
			}
		}
	}
}
