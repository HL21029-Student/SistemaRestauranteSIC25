<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Página de Bienvenida - Inventario</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-image: url('https://img.freepik.com/foto-gratis/vista-superior-martillo-acero-otros-elementos-construccion-herramientas_23-2150576461.jpg?t=st=1762982729~exp=1762986329~hmac=b4dbb20da90d9ba5cb5b11b23d132bcd19eda746baa8b3e5836a3a824c03a668&w=1480');
            background-size: cover;
            background-position: center;
            color: #cdcbcb;
            text-align: center;
            margin: 0;
            padding: 0;
            height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            box-sizing: border-box;
        }

        h1 {
            font-size: 3.5em;
            color: #cdcbcb;
            text-shadow: 3px 3px 10px rgba(0, 0, 0, 0.8);
            margin-bottom: 20px;
            padding: 10px;
            background-color: rgba(0, 0, 0, 0.5); /* Fondo semitransparente */
            border-radius: 10px;
        }

        p {
            font-size: 1.5em;
            color: #cdcbcb;
            margin-bottom: 30px;
            text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.7);
            padding: 10px;
            background-color: rgba(0, 0, 0, 0.5); /* Fondo semitransparente */
            border-radius: 10px;
        }

        .icon {
            font-size: 50px;
            color: #f39c12;
            margin: 10px;
            text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.7);
        }

        .btn-iniciar {
            background-color: chocolate;
            color: #cdcbcb;
            border: none;
            padding: 20px 40px;
            font-size: 1.5em;
            cursor: pointer;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
            transition: background-color 0.3s ease;
        }

        .btn-iniciar:hover {
            background-color: #c0392b;
        }

        form {
            margin-top: 30px;
        }

        /* Para los íconos */
        .icon-container {
            margin-top: 20px;
        }
    </style>
</head>
<body>
<h1>Bienvenido al Sistema de Inventario Web</h1>
<p>¡El control de su mercancia desde cualquier lugar!</p>
<div class="icon-container">
    <i class=""></i>
</div>
<form action="paginas/TipoAlmacen.jsf" method="get">
    <button class="btn-iniciar">Iniciar</button>
</form>
</body>
</html>
