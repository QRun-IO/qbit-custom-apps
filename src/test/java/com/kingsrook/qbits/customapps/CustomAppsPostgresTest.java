/*
 * QQQ - Low-code Application Framework for Engineers.
 * Copyright (C) 2021-2025.  Kingsrook, LLC
 * 651 N Broad St Ste 205 # 6917 | Middletown DE 19709 | United States
 * contact@kingsrook.com
 * https://github.com/Kingsrook/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kingsrook.qbits.customapps;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;
import com.kingsrook.qbits.customapps.metadata.CustomAppsDynamicMetaDataProducer;
import com.kingsrook.qbits.customapps.metadata.CustomAppsMetaDataActionCustomizer;
import com.kingsrook.qbits.customapps.model.CustomApp;
import com.kingsrook.qbits.customapps.model.CustomAppBackendEnum;
import com.kingsrook.qbits.userrolepermissions.UserRolePermissionsQBitConfig;
import com.kingsrook.qbits.userrolepermissions.UserRolePermissionsQBitProducer;
import com.kingsrook.qqq.backend.core.actions.dashboard.RenderWidgetAction;
import com.kingsrook.qqq.backend.core.actions.dashboard.widgets.AbstractWidgetRenderer;
import com.kingsrook.qqq.backend.core.actions.metadata.MetaDataAction;
import com.kingsrook.qqq.backend.core.actions.tables.CountAction;
import com.kingsrook.qqq.backend.core.actions.tables.InsertAction;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.exceptions.QPermissionDeniedException;
import com.kingsrook.qqq.backend.core.instances.QInstanceValidator;
import com.kingsrook.qqq.backend.core.model.actions.metadata.MetaDataInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.count.CountInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.insert.InsertInput;
import com.kingsrook.qqq.backend.core.model.actions.widgets.RenderWidgetInput;
import com.kingsrook.qqq.backend.core.model.actions.widgets.RenderWidgetOutput;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.StatisticsData;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.WidgetType;
import com.kingsrook.qqq.backend.core.model.data.QRecord;
import com.kingsrook.qqq.backend.core.model.metadata.QAuthenticationType;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.authentication.QAuthenticationMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.dashboard.QWidgetMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.fields.QFieldType;
import com.kingsrook.qqq.backend.core.model.metadata.permissions.PermissionLevel;
import com.kingsrook.qqq.backend.core.model.metadata.permissions.QPermissionRules;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.ProvidedOrSuppliedTableConfig;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.model.session.QSession;
import com.kingsrook.qqq.backend.core.model.session.QSystemUserSession;
import com.kingsrook.qqq.backend.module.postgres.model.metadata.PostgreSQLBackendMetaData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/*******************************************************************************
 ** Custom apps on a real PostgreSQL 17: the schema owner creates the tables, the
 ** application runs as a DML-only role, apps declared as rows reach the frontend
 ** metadata per session, a custom screen shows a widget over local data, and the
 ** apps' permissions (rows of the permission table the roles qbit manages) decide
 ** who sees and renders it.
 *******************************************************************************/
class CustomAppsPostgresTest
{
   private static final String OWNER   = "ca_owner";
   private static final String RUNTIME = "ca_runtime";
   private static final String BACKEND = "customAppsPg";
   private static final String ITEMS   = "demoItem";
   private static final String WIDGET  = "demoItemCountWidget";

   private static GenericContainer<?> postgres;
   private static String              host;
   private static int                 port;



   /*******************************************************************************
    ** PG17 with an owner-created schema (derived from the installed metadata) and a
    ** DML-only runtime role.
    *******************************************************************************/
   @BeforeAll
   static void start() throws Exception
   {
      postgres = new GenericContainer<>("postgres:17-alpine")
         .withEnv("POSTGRES_PASSWORD", "super-synthetic")
         .withExposedPorts(5432)
         .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 2).withStartupTimeout(java.time.Duration.ofMinutes(2)));
      postgres.start();
      host = postgres.getHost();
      port = postgres.getMappedPort(5432);

      try(Connection admin = connect("postgres", "super-synthetic", "postgres"); Statement statement = admin.createStatement())
      {
         statement.execute("CREATE ROLE " + OWNER + " LOGIN PASSWORD 'owner-synthetic'");
         statement.execute("CREATE ROLE " + RUNTIME + " LOGIN PASSWORD 'runtime-synthetic'");
         statement.execute("CREATE DATABASE customapps OWNER " + OWNER);
         statement.execute("REVOKE ALL ON DATABASE customapps FROM PUBLIC");
         statement.execute("GRANT CONNECT ON DATABASE customapps TO " + RUNTIME);
      }

      QInstance scratch = instance(OWNER, "owner-synthetic");
      try(Connection owner = connect(OWNER, "owner-synthetic", "customapps"); Statement statement = owner.createStatement())
      {
         statement.execute("REVOKE ALL ON SCHEMA public FROM PUBLIC");
         statement.execute("GRANT USAGE ON SCHEMA public TO " + RUNTIME);
         for(QTableMetaData table : scratch.getTables().values())
         {
            if(BACKEND.equals(table.getBackendName()))
            {
               statement.execute(TestDdl.create(table));
               statement.execute("GRANT SELECT, INSERT, UPDATE, DELETE ON \"" + table.getName() + "\" TO " + RUNTIME);
            }
         }
      }
   }



   /*******************************************************************************
    ** Stop the database.
    *******************************************************************************/
   @AfterAll
   static void stop()
   {
      if(postgres != null)
      {
         postgres.stop();
      }
   }



   /*******************************************************************************
    ** Clear the context.
    *******************************************************************************/
   @AfterEach
   void clear()
   {
      QContext.clear();
   }



   /*******************************************************************************
    ** Declared rows reach the frontend metadata per session; the screen shows a
    ** widget over local data; the app is closed to anyone without its permission;
    ** invalid rows are skipped rather than taking the others down.
    *******************************************************************************/
   @Test
   void testDeclaredAppRendersAndIsPermissioned() throws Exception
   {
      QInstance instance = instance(RUNTIME, "runtime-synthetic");
      QContext.init(instance, new QSystemUserSession());
      seed();

      /////////////////////////////////////////////////////////////////////////////////
      // the customizers created one permission per app and container (URP rows)     //
      /////////////////////////////////////////////////////////////////////////////////
      assertThat(new CountAction().execute(new CountInput("permission")).getCount()).isGreaterThanOrEqualTo(2);

      var all = new CustomAppsDynamicMetaDataProducer().getCustomAppList(instance, name -> true);
      assertThat(all.appMap()).containsKey("qcaOperationsBoard");
      assertThat(all.appMap()).doesNotContainKeys("qcaMissingWidget", "qcaGeneratedName", "qcaNoWidget");
      assertThat(all.appMap().get("qcaOperationsBoard").getWidgets()).containsExactly(WIDGET);
      assertThat(all.appMap().get("qcaOperationsBoard").getPermissionRules().getLevel()).isEqualTo(PermissionLevel.HAS_ACCESS_PERMISSION);

      /////////////////////////////////////////////////
      // a user without the app permission sees none //
      /////////////////////////////////////////////////
      QContext.init(instance, new QSession().withPermissions(Set.of("unrelated.read")));
      var nothing = new MetaDataAction().execute(new MetaDataInput());
      assertThat(nothing.getApps()).doesNotContainKeys("qcaOperationsBoard", "qcacOperations");
      assertThat(nothing.getAppTree().stream().map(n -> n.getName())).doesNotContain("qcacOperations");
      assertThatThrownBy(() -> new RenderWidgetAction().execute(new RenderWidgetInput().withWidgetMetaData(instance.getWidget(WIDGET))))
         .isInstanceOf(QPermissionDeniedException.class);

      /////////////////////////////////////////////////////////////////////////////////
      // a user granted the app (and its container) sees it and renders real DB data //
      /////////////////////////////////////////////////////////////////////////////////
      QContext.init(instance, new QSession().withPermissions(Set.of("qcaOperationsBoard.hasAccess", "qcacOperations.hasAccess", ITEMS + ".read")));
      var visible = new MetaDataAction().execute(new MetaDataInput());
      assertThat(visible.getApps()).containsKeys("qcaOperationsBoard", "qcacOperations");
      assertThat(visible.getWidgets()).containsKey(WIDGET);
      assertThat(visible.getAppTree().stream().map(n -> n.getName())).contains("qcacOperations");
      RenderWidgetOutput rendered = new RenderWidgetAction().execute(new RenderWidgetInput().withWidgetMetaData(instance.getWidget(WIDGET)));
      assertThat(((StatisticsData) rendered.getWidgetData()).getCount()).isEqualTo(3);

      ////////////////////////////////////////////////////////////////////////////
      // a user granted the app but not the container does not see it in the tree //
      ////////////////////////////////////////////////////////////////////////////
      QContext.init(instance, new QSession().withPermissions(Set.of("qcaOperationsBoard.hasAccess")));
      assertThat(new MetaDataAction().execute(new MetaDataInput()).getAppTree().stream().map(n -> n.getName())).doesNotContain("qcacOperations");
   }



   /*******************************************************************************
    ** The runtime role does DML only.
    *******************************************************************************/
   @Test
   void testRuntimeRoleHasNoDdl() throws Exception
   {
      try(Connection runtime = connect(RUNTIME, "runtime-synthetic", "customapps"); Statement statement = runtime.createStatement())
      {
         assertThatThrownBy(() -> statement.execute("CREATE TABLE intruder (id int)")).isInstanceOf(SQLException.class).hasMessageContaining("permission denied");
         assertThatThrownBy(() -> statement.execute("DROP TABLE \"" + CustomApp.TABLE_NAME + "\"")).isInstanceOf(SQLException.class);
      }
   }



   /*******************************************************************************
    ** Component bundles are code the browser runs: only paths on this application,
    ** or https hosts the configuration lists, are accepted.
    *******************************************************************************/
   @Test
   void testComponentSourceIsRestricted() throws Exception
   {
      QInstance instance = instance(RUNTIME, "runtime-synthetic");
      QContext.init(instance, new QSystemUserSession());
      CustomAppsDynamicMetaDataProducer producer = new CustomAppsDynamicMetaDataProducer();
      assertThat(producer.isAllowedComponentSource("/dynamic-qfmd-components/q-embed-looker-dashboards.bundle.js")).isTrue();
      assertThat(producer.isAllowedComponentSource("//evil.example/x.js")).isFalse();
      assertThat(producer.isAllowedComponentSource("https://evil.example/x.js")).isFalse();
      assertThat(producer.isAllowedComponentSource("javascript:alert(1)")).isFalse();
      assertThat(producer.isAllowedComponentSource("data:text/javascript,alert(1)")).isFalse();
      assertThat(producer.isAllowedComponentSource("/ok/../../etc/passwd")).isFalse();
      assertThat(producer.isAllowedComponentSource("/a b.js")).isFalse();
      assertThat(producer.isAllowedComponentSource(null)).isFalse();

      QInstance allowing = instance(RUNTIME, "runtime-synthetic", List.of("Cdn.Example.com"));
      QContext.init(allowing, new QSystemUserSession());
      assertThat(producer.isAllowedComponentSource("https://cdn.example.com/x.js")).isTrue();
      assertThat(producer.isAllowedComponentSource("https://cdn.example.com@evil.example/x.js")).isFalse();
      assertThat(producer.isAllowedComponentSource("http://cdn.example.com/x.js")).isFalse();
   }



   /*******************************************************************************
    ** The configuration requires its backend.
    *******************************************************************************/
   @Test
   void testConfigValidation() throws Exception
   {
      List<String> errors = new java.util.ArrayList<>();
      new CustomAppsQBitConfig().validate(new QInstance(), errors);
      assertThat(errors).anyMatch(e -> e.contains("backendName"));
   }



   /*******************************************************************************
    ** Synthetic definitions: one valid local-widget app and three invalid ones.
    *******************************************************************************/
   private static void seed() throws QException
   {
      insert("customAppIcon", new QRecord().withValue("id", 1).withValue("name", "Dashboard").withValue("iconId", "dashboard"));
      insert("customAppBackendConfig", new QRecord().withValue("id", 1).withValue("name", "Local widget").withValue("customAppBackendId", CustomAppBackendEnum.LOCAL_WIDGET.getId()));
      insert("customAppContainer", new QRecord().withValue("id", 1).withValue("name", "Operations").withValue("customAppIconId", 1).withValue("sequenceNo", 1));
      insert("customAppSection", new QRecord().withValue("id", 1).withValue("name", "Boards").withValue("customAppContainerId", 1).withValue("sequenceNo", 1));
      insertApp(1, "Operations Board", WIDGET);
      insertApp(2, "Missing Widget", "noSuchWidget");
      insertApp(3, "Generated Name", "qcaOperationsBoardWidget");
      insertApp(4, "No Widget", null);
      for(int i = 1; i <= 3; i++)
      {
         insert(ITEMS, new QRecord().withValue("name", "Item " + i));
      }
   }



   /*******************************************************************************
    ** One app definition row.
    *******************************************************************************/
   private static void insertApp(int id, String name, String widgetName) throws QException
   {
      insert("customApp", new QRecord().withValue("id", id).withValue("name", name).withValue("customAppBackendConfigId", 1).withValue("customAppSectionId", 1)
         .withValue("customAppIconId", 1).withValue("sequenceNo", id).withValue("widgetName", widgetName));
   }



   /*******************************************************************************
    ** Insert a record.
    *******************************************************************************/
   private static void insert(String table, QRecord record) throws QException
   {
      List<QRecord> records = new InsertAction().execute(new InsertInput(table).withRecord(record)).getRecords();
      assertThat(records.get(0).getErrors()).isEmpty();
   }



   /*******************************************************************************
    ** An application on the given role.
    *******************************************************************************/
   private static QInstance instance(String user, String password) throws QException
   {
      return instance(user, password, List.of());
   }



   /*******************************************************************************
    ** An application: native PG backend as a role, the roles qbit (whose permission
    ** table the apps use), this qbit with its metadata customizer, a local table and
    ** a permissioned widget over it.
    *******************************************************************************/
   private static QInstance instance(String user, String password, List<String> allowedHosts) throws QException
   {
      QInstance instance = new QInstance();
      instance.setEnforcePermissionsInCoreActions(true);
      instance.withInstanceDefaultAuthentication(new QAuthenticationMetaData().withName("anonymous").withType(QAuthenticationType.FULLY_ANONYMOUS));
      PostgreSQLBackendMetaData backend = new PostgreSQLBackendMetaData();
      backend.setName(BACKEND);
      backend.setHostName(host);
      backend.setPort(port);
      backend.setDatabaseName("customapps");
      backend.setUsername(user);
      backend.setPassword(password);
      instance.addBackend(backend);

      new UserRolePermissionsQBitProducer().withUserRolePermissionsQBitConfig(new UserRolePermissionsQBitConfig()
         .withUserTableConfig(ProvidedOrSuppliedTableConfig.provideTableUsingBackendNamed(BACKEND))
         .withTableMetaDataCustomizer((i, table) ->
         {
            table.setBackendName(BACKEND);
            return (table);
         })).produce(instance);
      new CustomAppsQBitProducer().withQBitConfig(new CustomAppsQBitConfig().withBackendName(BACKEND).withAllowedComponentHosts(allowedHosts)).produce(instance).addSelfToInstance(instance);
      instance.setMetaDataActionCustomizer(new QCodeReference(CustomAppsMetaDataActionCustomizer.class));

      instance.addTable(new QTableMetaData().withName(ITEMS).withBackendName(BACKEND).withPrimaryKeyField("id")
         .withPermissionRules(new QPermissionRules().withLevel(PermissionLevel.READ_WRITE_PERMISSIONS).withPermissionBaseName(ITEMS))
         .withField(new QFieldMetaData("id", QFieldType.INTEGER).withIsEditable(false))
         .withField(new QFieldMetaData("name", QFieldType.STRING).withMaxLength(100)));
      instance.addWidget(new QWidgetMetaData().withName(WIDGET).withType(WidgetType.STATISTICS.getType())
         .withCodeReference(new QCodeReference(ItemCountWidgetRenderer.class))
         .withPermissionRules(new QPermissionRules().withLevel(PermissionLevel.HAS_ACCESS_PERMISSION).withPermissionBaseName("qcaOperationsBoard")));
      new QInstanceValidator().validate(instance);
      return instance;
   }



   /*******************************************************************************
    ** A JDBC connection as a named role.
    *******************************************************************************/
   private static Connection connect(String user, String password, String database) throws SQLException
   {
      return DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database, user, password);
   }



   /*******************************************************************************
    ** A widget that counts the rows of the local table, as the viewing user.
    *******************************************************************************/
   public static class ItemCountWidgetRenderer extends AbstractWidgetRenderer
   {
      /***************************************************************************
       ** Render the count.
       ***************************************************************************/
      @Override
      public RenderWidgetOutput render(RenderWidgetInput input) throws QException
      {
         Integer count = new CountAction().execute(new CountInput(ITEMS)).getCount();
         return (new RenderWidgetOutput(new StatisticsData().withCount(count)));
      }
   }
}
