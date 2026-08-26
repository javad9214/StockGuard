package ir.yar.anbar.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Version 3 shipped with two different physical schemas: the `image` column was
 * split into `imageLocalPath` + `imageUrl` without a version bump, so installed
 * v3 databases have either shape. MIGRATION_3_4 self-heals both: it rebuilds the
 * table only when the legacy `image` column is present, and always creates the
 * indices used by the server-sync lookups (matched by serverId / barcode).
 *
 * Versions 1 and 2 predate server sync; their rows are local-only and cannot be
 * mapped reliably, so DatabaseModule falls back to a destructive migration for
 * them — the server copy is re-pulled on the next product list load.
 */
val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val columns = db.query("SELECT name FROM pragma_table_info('user_products')").use { cursor ->
            buildSet {
                while (cursor.moveToNext()) add(cursor.getString(0))
            }
        }

        if ("image" in columns && "imageLocalPath" !in columns) {
            // DROP COLUMN needs SQLite 3.35 (Android 14+); minSdk 26 forces the
            // portable recreate-copy-rename path instead.
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `_new_user_products` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `serverId` INTEGER,
                `catalogProductId` INTEGER,
                `name` TEXT NOT NULL,
                `barcode` TEXT,
                `customName` TEXT,
                `price` INTEGER NOT NULL,
                `costPrice` INTEGER NOT NULL,
                `description` TEXT,
                `imageLocalPath` TEXT,
                `imageUrl` TEXT,
                `subcategoryId` INTEGER,
                `supplierId` INTEGER,
                `unit` TEXT,
                `stock` INTEGER NOT NULL,
                `minStockLevel` INTEGER,
                `maxStockLevel` INTEGER,
                `isActive` INTEGER NOT NULL,
                `tags` TEXT,
                `lastSoldDate` INTEGER,
                `date` INTEGER NOT NULL,
                `syncStatus` TEXT NOT NULL,
                `synced` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                `isDeleted` INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO `_new_user_products` (`id`,`serverId`,`catalogProductId`,`name`,`barcode`,
                `customName`,`price`,`costPrice`,`description`,`imageLocalPath`,`imageUrl`,`subcategoryId`,
                `supplierId`,`unit`,`stock`,`minStockLevel`,`maxStockLevel`,`isActive`,`tags`,`lastSoldDate`,
                `date`,`syncStatus`,`synced`,`createdAt`,`updatedAt`,`isDeleted`)
                SELECT `id`,`serverId`,`catalogProductId`,`name`,`barcode`,`customName`,`price`,`costPrice`,
                `description`,`image`,NULL,`subcategoryId`,`supplierId`,`unit`,`stock`,`minStockLevel`,
                `maxStockLevel`,`isActive`,`tags`,`lastSoldDate`,`date`,`syncStatus`,`synced`,`createdAt`,
                `updatedAt`,`isDeleted`
                FROM `user_products`
                """.trimIndent()
            )
            db.execSQL("DROP TABLE `user_products`")
            db.execSQL("ALTER TABLE `_new_user_products` RENAME TO `user_products`")
        }

        db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_products_serverId` ON `user_products` (`serverId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_products_barcode` ON `user_products` (`barcode`)")
    }
}

/**
 * v5 caches the subcategory display name on each row. The server now sends it
 * with the product list, and the local `subcategories` table is never synced,
 * so persisting it is the only way list UIs can render it offline. Rows
 * created before v5 stay null until the next server pull fills them in.
 */
val MIGRATION_4_5: Migration = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `user_products` ADD COLUMN `subcategoryName` TEXT")
    }
}

/**
 * v6 links invoices to their server rows for invoice sync. Existing rows are
 * local-only (serverId stays null); they get a serverId the first time the
 * push-sync uploads them.
 */
val MIGRATION_5_6: Migration = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `invoices` ADD COLUMN `serverId` INTEGER")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_invoices_serverId` ON `invoices` (`serverId`)")
    }
}
