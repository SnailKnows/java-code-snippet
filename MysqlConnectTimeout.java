public Connection getConnection() throws SQLException {
	Properties properties = new Properties();
	properties.setProperty("user", user);
	properties.setProperty("password", password);
	int timeout = 5*60*1000;
	properties.put("connectTimeout", "" + timeout);
	Connection connection = DriverManager.getConnection(jdbcURL, properties);
	return connection;
}
