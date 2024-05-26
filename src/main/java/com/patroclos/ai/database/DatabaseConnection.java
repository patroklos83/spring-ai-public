package com.patroclos.ai.database;

import java.math.BigInteger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.patroclos.ai.model.Document;
import com.pgvector.PGvector;

public class DatabaseConnection {

	public static Connection conn;

	private DatabaseConnection() {
	}

	static {
		try {
			getDatabaseConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void getDatabaseConnection() throws SQLException {
		String url = "jdbc:postgresql://localhost:5433/postgres?currentSchema=embedding&sslmode=disable";
		Properties props = new Properties();
		props.setProperty("user", "postgres");
		props.setProperty("password", "postgres");
		props.setProperty("ssl", "true");
		conn = DriverManager.getConnection(url, props);

		Statement setupStmt = conn.createStatement();
		setupStmt.executeUpdate("CREATE EXTENSION IF NOT EXISTS vector");
	
//		CREATE TABLE embedding.documents (
//				id bigserial NOT NULL,
//				"content" text NULL,
//				embedding embedding.vector NULL,
//				texthash varchar(255) NULL,
//				CONSTRAINT documents_pkey PRIMARY KEY (id)
//			);
		PGvector.addVectorType(conn);
	}

	public static void saveDocument(Document document) throws SQLException, NoSuchAlgorithmException {
		int id = getExisting(document);
		if (id > 0) {
			PreparedStatement stat = conn.prepareStatement("UPDATE documents SET embedding = ?, texthash = ? WHERE ID = ?;");		
			stat.setObject(1, new PGvector(document.getVector()));
			stat.setString(2, getHash(document.getContent()));
			stat.setInt(3, id);
			stat.executeUpdate();
		}
		else {
			PreparedStatement stat = conn.prepareStatement("INSERT INTO documents(content, embedding, texthash) VALUES (?, ?, ?);");
			stat.setString(1, document.getContent());
			stat.setObject(2, new PGvector(document.getVector()));
			stat.setString(3, getHash(document.getContent()));
			stat.executeUpdate();
		}
	}

	public static int getExisting(Document document) throws SQLException, NoSuchAlgorithmException {
		PreparedStatement stmt = conn.prepareStatement("SELECT id FROM documents WHERE texthash = ?");
		stmt.setString(1, getHash(document.getContent()));
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return rs.getInt(1);
		}
		return 0;
	}

	public static void getNearest(Document document) throws SQLException {
		PreparedStatement neighborStmt = conn.prepareStatement(""
				+ " SELECT *, 1 - (embedding <=> ?) AS cosine_similarity"
				+ " FROM documents "
				+ " ORDER BY cosine_similarity desc limit 5"); //embedding <-> ? LIMIT 5");
		neighborStmt.setObject(1, new PGvector(document.getVector()));
		//neighborStmt.setObject(2, new PGvector(document.getVector()));
		ResultSet rs = neighborStmt.executeQuery();
		while (rs.next()) {
			System.out.println("\n\n\n");
			System.out.println(rs.getObject("cosine_similarity").toString());
			System.out.println();
			System.out.println(rs.getObject("content"));
		}
	}

	public static String getHash(String text) throws NoSuchAlgorithmException
	{
		final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
		final byte[] hashbytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));

		// Convert byte array into signum representation
		BigInteger number = new BigInteger(1, hashbytes);

		// Convert message digest into hex value
		StringBuilder hexString = new StringBuilder(number.toString(16));

		// Pad with leading zeros
		while (hexString.length() < 64)
		{
			hexString.insert(0, '0');
		}

		return hexString.toString();
	}

}
