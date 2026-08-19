import dotenv from 'dotenv';
import { createApp } from './app';

dotenv.config();

/**
 * AgriSync API Server Entry Point
 * Implements graceful shutdown listeners and process signal handling (SIGINT/SIGTERM)
 * inspired by enterprise OQMS production server architecture.
 */
async function bootstrap(): Promise<void> {
  try {
    const app = createApp();
    const PORT = process.env.PORT || 3000;

    const server = app.listen(PORT, () => {
      console.log(`
╔══════════════════════════════════════════════════════╗
║        AgriSync Bright Data DCA Proxy Server         ║
╠══════════════════════════════════════════════════════╣
║  Environment : ${(process.env.NODE_ENV || 'development').padEnd(37)} ║
║  Port        : ${String(PORT).padEnd(37)} ║
║  Collector ID: c_apmc_spice_v1_09x                     ║
║  API Base    : /api/sync/trigger                     ║
╚══════════════════════════════════════════════════════╝
      `);
    });

    // ─── Graceful Shutdown Handlers ─────────────────────────────────────
    const shutdown = async (signal: string): Promise<void> => {
      console.log(`\n${signal} received. Closing AgriSync HTTP server gracefully...`);

      server.close(() => {
        console.log('HTTP server closed cleanly. Goodbye.');
        process.exit(0);
      });

      // Force exit if hanging after 10 seconds
      setTimeout(() => {
        console.error('Forced shutdown after 10s timeout.');
        process.exit(1);
      }, 10000);
    };

    process.on('SIGTERM', () => shutdown('SIGTERM'));
    process.on('SIGINT', () => shutdown('SIGINT'));

    // ─── Global Error Handlers ───────────────────────────────────────────
    process.on('unhandledRejection', (reason: unknown) => {
      console.error('Unhandled Promise Rejection:', reason);
    });

    process.on('uncaughtException', (error: Error) => {
      console.error('Uncaught Exception:', error);
      process.exit(1);
    });
  } catch (error) {
    console.error('Failed to bootstrap AgriSync server:', error);
    process.exit(1);
  }
}

bootstrap();
