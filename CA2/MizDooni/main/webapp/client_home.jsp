<jsp:include page="header.jsp" />
<head>
    <meta charset="UTF-8" />
    <title>Client Home</title>
  </head>
  <body>
    <h1>
      Welcome ${username} <a href="/logout" style="color: red">Log Out</a>
    </h1>

    <ul type="square">
      <li>
        <a href="/restaurants">Restaurants</a>
      </li>
      <li>
        <a href="/reservations">Reservations</a>
      </li>
    </ul>
  </body>
<jsp:include page="footer.jsp" />