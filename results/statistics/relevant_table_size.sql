SELECT t.name 'Table Name', i.rows 'Rows number'

FROM sysobjects t INNER JOIN sysindexes i ON (t.id = i.id)

WHERE t.xtype = 'U' AND i.indid in (0,1) AND i.rows != 0

ORDER BY 'Table Name';
