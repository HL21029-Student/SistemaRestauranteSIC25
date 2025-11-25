<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bienvenido - FERRETERIA SIC25</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #1a202c 0%, #2d3748 50%, #1a202c 100%);
            color: #ffffff;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            overflow-x: hidden;
        }

        /* Header */
        .header {
            background: rgba(0, 0, 0, 0.3);
            backdrop-filter: blur(10px);
            padding: 1.5rem 3rem;
            display: flex;
            align-items: center;
            justify-content: space-between;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
        }

        .logo-section {
            display: flex;
            align-items: center;
            gap: 1rem;
        }

        .logo-icon {
            width: 60px;
            height: 60px;
            background: #F39A3C;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2rem;
            box-shadow: 0 4px 15px rgba(243, 154, 60, 0.4);
        }

        .logo-text h1 {
            font-size: 1.75rem;
            font-weight: 700;
            color: #ffffff;
            margin-bottom: 0.25rem;
        }

        .logo-text p {
            font-size: 0.875rem;
            color: rgba(255, 255, 255, 0.6);
            font-weight: 400;
        }

        /* Main Content */
        .main-content {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 3rem 2rem;
            position: relative;
        }

        /* Background Pattern */
        .background-pattern {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-image:
                radial-gradient(circle at 20% 30%, rgba(243, 154, 60, 0.05) 0%, transparent 50%),
                radial-gradient(circle at 80% 70%, rgba(243, 154, 60, 0.05) 0%, transparent 50%);
            pointer-events: none;
        }

        /* Welcome Card */
        .welcome-card {
            position: relative;
            z-index: 1;
            max-width: 900px;
            width: 100%;
            text-align: center;
        }

        .welcome-card h2 {
            font-size: 3.5rem;
            font-weight: 700;
            margin-bottom: 1rem;
            background: linear-gradient(135deg, #ffffff 0%, #F39A3C 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            line-height: 1.2;
        }

        .welcome-card .subtitle {
            font-size: 1.5rem;
            color: rgba(255, 255, 255, 0.8);
            margin-bottom: 3rem;
            font-weight: 300;
        }

        /* Features Grid */
        .features-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1.5rem;
            margin-bottom: 3rem;
        }

        .feature-item {
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 12px;
            padding: 2rem 1.5rem;
            transition: all 0.3s ease;
        }

        .feature-item:hover {
            background: rgba(243, 154, 60, 0.1);
            border-color: rgba(243, 154, 60, 0.3);
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(243, 154, 60, 0.2);
        }

        .feature-item i {
            font-size: 2.5rem;
            color: #F39A3C;
            margin-bottom: 1rem;
            display: block;
        }

        .feature-item h3 {
            font-size: 1.125rem;
            color: #ffffff;
            margin-bottom: 0.5rem;
            font-weight: 600;
        }

        .feature-item p {
            font-size: 0.875rem;
            color: rgba(255, 255, 255, 0.6);
            line-height: 1.5;
        }

        /* CTA Button */
        .btn-iniciar {
            display: inline-flex;
            align-items: center;
            gap: 0.75rem;
            background: linear-gradient(135deg, #F39A3C 0%, #e67e22 100%);
            color: #ffffff;
            border: none;
            padding: 1.25rem 3rem;
            font-size: 1.25rem;
            font-weight: 600;
            cursor: pointer;
            border-radius: 12px;
            box-shadow: 0 8px 25px rgba(243, 154, 60, 0.4);
            transition: all 0.3s ease;
            text-decoration: none;
        }

        .btn-iniciar:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 35px rgba(243, 154, 60, 0.5);
        }

        .btn-iniciar i {
            font-size: 1.5rem;
        }

        /* Footer */
        .footer {
            background: rgba(0, 0, 0, 0.3);
            backdrop-filter: blur(10px);
            padding: 1.5rem 3rem;
            text-align: center;
            color: rgba(255, 255, 255, 0.6);
            font-size: 0.875rem;
            box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.2);
        }

        /* Responsive */
        @media (max-width: 768px) {
            .header {
                padding: 1rem 1.5rem;
                flex-direction: column;
                gap: 1rem;
            }

            .welcome-card h2 {
                font-size: 2.5rem;
            }

            .welcome-card .subtitle {
                font-size: 1.125rem;
            }

            .features-grid {
                grid-template-columns: 1fr;
            }

            .btn-iniciar {
                width: 100%;
                justify-content: center;
            }
        }
    </style>
</head>
<body>
    <!-- Header -->
    <div class="header">
        <div class="logo-section">
            <div class="logo-icon">
                🔧
            </div>
            <div class="logo-text">
                <h1>FERRETERIA SIC25</h1>
                <p>Sistema de Gestión Integral</p>
            </div>
        </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
        <div class="background-pattern"></div>

        <div class="welcome-card">
            <h2>Bienvenido al Sistema</h2>
            <p class="subtitle">Control total de inventario, ventas y contabilidad desde cualquier lugar</p>

            <!-- Features Grid -->
            <div class="features-grid">
                <div class="feature-item">
                    <i class="fas fa-boxes"></i>
                    <h3>Gestión de Inventario</h3>
                    <p>Control completo de productos y almacenes</p>
                </div>
                <div class="feature-item">
                    <i class="fas fa-shopping-cart"></i>
                    <h3>Ventas y Compras</h3>
                    <p>Registro y seguimiento de transacciones</p>
                </div>
                <div class="feature-item">
                    <i class="fas fa-file-invoice-dollar"></i>
                    <h3>Contabilidad</h3>
                    <p>Libros diarios, mayores y reportes</p>
                </div>
                <div class="feature-item">
                    <i class="fas fa-chart-line"></i>
                    <h3>Reportes</h3>
                    <p>Análisis detallado de tu negocio</p>
                </div>
            </div>

            <!-- CTA Button -->
            <a href="paginas/LibroDiario.jsf" class="btn-iniciar">
                <i class="fas fa-arrow-right"></i>
                <span>Iniciar Sistema</span>
            </a>
        </div>
    </div>

    <!-- Footer -->
    <div class="footer">
        © 2025 FERRETERIA SIC25 - Universidad de El Salvador - Todos los derechos reservados
    </div>
</body>
</html>
