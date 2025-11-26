<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Restaurante El Buen Sabor - Sistema de Gestión</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        :root{
            /* --- PALETA ACTUALIZADA --- */
            /* Usamos el mismo rojo (#D32F2F) del diseño horizontal */
            --brand-primary: #D32F2F;
            --brand-accent:  #B71C1C; /* Rojo un poco más oscuro para el gradiente */
            --brand-contrast:#FFFFFF;
            --brand-muted:   #F8F6F6;
            --text-dark:     #222222;

            /* Tamaños de logo */
            --logo-desktop:  6rem;
            --logo-tablet:   4.5rem;
            --logo-mobile:   3rem;

            /* Botones */
            --btn-padding:   0.9rem 2.5rem;
            --btn-radius:    10px;
            /* Botones en amarillo brillante */
            --brand-btn: #FFD700;
            --brand-btn-accent: #FFC107;
            --brand-btn-contrast: #212121;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        html, body {
            height: 100%;
            width: 100%;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #0f0f0f;
        }

        body {
            color: #E0E0E0;
        }

        /* Contenedor Principal */
        .hero-container {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 2rem;
            /* Fondo con el gradiente de los nuevos rojos */
            background: linear-gradient(135deg, var(--brand-primary) 0%, var(--brand-accent) 100%);
            background-attachment: fixed;
            text-align: center;
            position: relative;
            overflow: hidden;
        }

        .hero-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: radial-gradient(circle at top right, rgba(255,255,255,0.02), transparent 40%),
            radial-gradient(circle at bottom left, rgba(0,0,0,0.06), transparent 50%);
            pointer-events: none;
        }

        .content-wrapper {
            position: relative;
            z-index: 1;
            max-width: 1000px;
            animation: fadeInUp 0.9s ease-out;
        }

        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(40px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* Logo y Branding */
        .logo-section {
            margin-bottom: 2rem;
        }

        .logo-icon {
            font-size: var(--logo-desktop);
            /* CAMBIO: Color amarillo para que resalte sobre el rojo */
            color: var(--brand-btn);
            margin-bottom: 1rem;
            text-shadow: 0 6px 28px rgba(0,0,0,0.25);
            animation: float 3s ease-in-out infinite;
            display: inline-block;
            line-height: 1;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0px); }
            50% { transform: translateY(-10px); }
        }

        .logo-section h1 {
            font-size: 4.5rem;
            font-weight: 800;
            color: var(--brand-contrast);
            margin-bottom: 0.5rem;
            letter-spacing: 2px;
            text-shadow: 0 6px 30px rgba(0,0,0,0.6);
        }

        .logo-section .subtitle {
            font-size: 1.3rem;
            color: rgba(255,255,255,0.9);
            font-weight: 300;
            letter-spacing: 2px;
            text-transform: uppercase;
            opacity: 0.95;
        }

        /* Divider */
        .divider {
            width: 120px;
            height: 3px;
            background: linear-gradient(90deg, transparent, var(--brand-primary), transparent);
            margin: 2rem auto;
            box-shadow: 0 0 12px rgba(0,0,0,0.18);
        }

        /* Descripción Principal */
        .description {
            margin: 2.5rem 0;
            padding: 0 1rem;
        }

        .description h2 {
            font-size: 2.3rem;
            color: #FDFBF4;
            margin-bottom: 1.5rem;
            font-weight: 700;
            line-height: 1.3;
        }

        .description p {
            font-size: 1.15rem;
            color: #D0D0D0;
            line-height: 1.9;
            margin-bottom: 1.2rem;
            text-align: center;
        }

        /* Botones */
        .buttons-container {
            display: flex;
            gap: 1.5rem;
            justify-content: center;
            flex-wrap: wrap;
            margin-top: 3rem;
            animation: slideInUp 0.9s ease-out 0.2s both;
        }

        @keyframes slideInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .btn-wrapper {
            display: inline-block;
        }

        .btn {
            padding: 18px 45px;
            font-size: 1.1rem;
            border: none;
            border-radius: var(--btn-radius);
            cursor: pointer;
            font-weight: 700;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 12px;
            box-shadow: 0 6px 25px rgba(0, 0, 0, 0.5);
            text-transform: uppercase;
            letter-spacing: 1px;
            outline: none;
        }

        .btn:hover {
            transform: translateY(-4px);
            box-shadow: 0 10px 35px rgba(0, 0, 0, 0.7);
        }

        .btn:active {
            transform: translateY(-1px);
        }

        /* Botón Principal - Ingresar */
        .btn-primary {
            background: linear-gradient(135deg, var(--brand-btn) 0%, var(--brand-btn-accent) 100%);
            color: var(--brand-btn-contrast);
            min-width: 220px;
            font-size: 1.2rem;
            box-shadow: 0 8px 20px rgba(0,0,0,0.15);
        }

        .btn-primary:hover {
            box-shadow: 0 12px 30px rgba(0,0,0,0.2);
            transform: translateY(-5px) scale(1.02);
        }

        /* Botones Secundarios */
        .btn-secondary {
            background: rgba(255,255,255,0.08);
            color: var(--brand-contrast);
            border: 2.5px solid rgba(255,255,255,0.12);
            min-width: 180px;
            backdrop-filter: blur(6px);
        }

        .btn-secondary:hover {
            background: rgba(255,255,255,0.12);
            border-color: rgba(255,255,255,0.18);
            color: var(--brand-contrast);
            box-shadow: 0 8px 30px rgba(0,0,0,0.12);
            transform: translateY(-4px);
        }

        /* Iconos de botones */
        .btn i {
            font-size: 1.4rem;
        }

        /* Feature Cards (Tarjetas inferiores) */
        .features {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 2rem;
            margin-top: 4rem;
            padding-top: 2rem;
            border-top: 2px solid rgba(255,255,255,0.1);
            animation: fadeInUp 1s ease-out 0.4s both;
        }

        .feature-card {
            background: linear-gradient(135deg, rgba(20,20,20,0.9) 0%, rgba(30,30,30,0.9) 100%);
            padding: 2rem;
            border-radius: 12px;
            /* Borde izquierdo rojo */
            border-left: 5px solid var(--brand-primary);
            transition: all 0.3s ease;
            backdrop-filter: blur(8px);
        }

        .feature-card:hover {
            background: linear-gradient(135deg, rgba(30,30,30,0.95) 0%, rgba(40,40,40,0.95) 100%);
            transform: translateY(-8px);
            border-color: var(--brand-btn); /* Borde amarillo al hover */
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
        }

        /* CAMBIO SOLICITADO: Iconos de las tarjetas en BLANCO */
        .feature-card i {
            font-size: 3rem;
            color: #FFFFFF; /* Blanco */
            margin-bottom: 1rem;
            transition: transform 0.3s ease;
        }

        .feature-card:hover i {
            transform: scale(1.1);
            color: var(--brand-btn); /* Se vuelven amarillos al pasar el mouse */
        }

        .feature-card h3 {
            color: var(--brand-primary);
            margin-bottom: 0.8rem;
            font-size: 1.3rem;
            font-weight: 700;
        }

        .feature-card p {
            color: #A0A0A0;
            font-size: 1rem;
            text-align: center;
            line-height: 1.6;
        }

        /* Footer */
        .footer-section {
            margin-top: 4rem;
            padding-top: 2rem;
            border-top: 2px solid rgba(255,255,255,0.1);
            font-size: 0.95rem;
            color: #B0B0B0;
            animation: fadeIn 1s ease-out 0.6s both;
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        .footer-section p {
            margin: 0.6rem 0;
        }

        .footer-section strong {
            color: var(--brand-btn);
        }

        /* Responsive */
        @media (max-width: 900px) {
            .logo-section h1 {
                font-size: 3.2rem;
            }

            .description h2 {
                font-size: 2rem;
            }
        }

        @media (max-width: 768px) {
            .hero-container {
                padding: 1.5rem;
            }

            .logo-section h1 {
                font-size: 2.5rem;
            }

            .logo-icon {
                font-size: var(--logo-tablet);
            }

            .description h2 {
                font-size: 1.7rem;
            }

            .description p {
                font-size: 1rem;
            }

            .buttons-container {
                gap: 1rem;
                flex-direction: column;
                align-items: center;
            }

            .btn {
                justify-content: center;
                width: 100%;
                max-width: 300px;
            }

            .features {
                grid-template-columns: 1fr;
                gap: 1.5rem;
                margin-top: 2rem;
            }
        }

        @media (max-width: 480px) {
            .logo-section h1 {
                font-size: 2rem;
            }

            .logo-icon {
                font-size: var(--logo-mobile);
            }

            .description h2 {
                font-size: 1.4rem;
            }

            .btn {
                padding: 15px 30px;
                font-size: 1rem;
            }

            .feature-card {
                padding: 1.5rem;
            }

            .feature-card i {
                font-size: 2.5rem;
            }
        }
    </style>
</head>
<body>
<div class="hero-container">
    <div class="content-wrapper">
        <div class="logo-section">
            <div class="logo-icon">🍽️</div>
            <h1>El Buen Sabor</h1>
            <p class="subtitle">Sistema de Gestión Integral</p>
        </div>

        <div class="divider"></div>

        <div class="description">
            <h2>Control Contable y Operativo del Restaurante</h2>
            <p>
                Centraliza todos tus registros financieros, gestiona tu inventario, controla compras y ventas,
                y genera reportes estratégicos desde una única plataforma. Potencia tu negocio con datos en tiempo real.
            </p>
        </div>

        <div class="buttons-container">
            <div class="btn-wrapper">
                <form action="paginas/LibroDiario.jsf" method="get" style="display: inline;">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-sign-in-alt"></i> Ingresar
                    </button>
                </form>
            </div>
            <div class="btn-wrapper">
                <form action="paginas/Reportes.jsf" method="get" style="display: inline;">
                    <button type="submit" class="btn btn-secondary">
                        <i class="fas fa-chart-bar"></i> Reportes
                    </button>
                </form>
            </div>
            <div class="btn-wrapper">
                <form action="paginas/Compras.jsf" method="get" style="display: inline;">
                    <button type="submit" class="btn btn-secondary">
                        <i class="fas fa-shopping-cart"></i> Compras
                    </button>
                </form>
            </div>
        </div>

        <div class="features">
            <div class="feature-card">
                <i class="fas fa-book-open"></i>
                <h3>Libro Diario</h3>
                <p>Registro detallado de todas las transacciones contables para un control financiero preciso.</p>
            </div>
            <div class="feature-card">
                <i class="fas fa-chart-line"></i>
                <h3>Análisis Financiero</h3>
                <p>Reportes estratégicos que te ayuden en la toma de decisiones empresariales inteligentes.</p>
            </div>
            <div class="feature-card">
                <i class="fas fa-boxes"></i>
                <h3>Inventario</h3>
                <p>Gestión completa de stock, existencias y control de materias primas.</p>
            </div>
        </div>

        <div class="footer-section">
            <p><strong>© 2025 Restaurante El Buen Sabor</strong></p>
            <p>Todos los derechos reservados - Universidad de El Salvador</p>
        </div>
    </div>
</div>
</body>
</html>