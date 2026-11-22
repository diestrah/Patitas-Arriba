<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>Patitas Arriba - Reportes</title>
</head>
<body>
<h1>Reportes de Patitas Arriba</h1>
<p>Cada reporte se genera en PDF con JasperReports. Ajusta los parámetros a datos que existan.</p>
<ul>
  <li><a href="reportes/agenda-diaria?fecha=2026-10-05">Agenda diaria</a> (RF12): <code>reportes/agenda-diaria?fecha=AAAA-MM-DD[&amp;veterinario={id}]</code></li>
  <li><a href="reportes/historial-clinico?mascota=1">Historial clínico</a> (RF15): <code>reportes/historial-clinico?mascota={id}</code></li>
  <li><a href="reportes/boleta?id=1">Boleta de venta</a> (RF16): <code>reportes/boleta?id={id}</code></li>
</ul>
</body>
</html>
