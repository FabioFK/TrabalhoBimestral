package br.univel;

import br.univel.anotacoes.Coluna;
import br.univel.anotacoes.Tabela;
import br.univel.SqlGen;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class Executa extends SqlGen {
    public Executa() {
    }

    @Override
    public String getCreateTable(Connection con, Object obj) {
        try {
            String nameTable;
            Class<?> cl = obj.getClass();

            StringBuilder sb = new StringBuilder();

            if (cl.isAnnotationPresent(Tabela.class)) {
                Tabela annotationTable = cl.getAnnotation(Tabela.class);
                nameTable = annotationTable.value();
            } else {
                nameTable = cl.getSimpleName().toUpperCase();
            }
            sb.append("CREATE TABLE ").append(nameTable).append(" (");

            Field[] attributes = cl.getDeclaredFields();

            for (int i = 0; i < attributes.length; i++) {
                Field field = attributes[i];

                String nameColumn;
                String typeColumn = null;

                if (field.isAnnotationPresent(Coluna.class)) {
                    Coluna annotationColumn = field.getAnnotation(Coluna.class);

                    if (annotationColumn.nome().isEmpty()) {
                        nameColumn = field.getName().toUpperCase();
                    } else {
                        nameColumn = annotationColumn.nome();
                    }
                } else {
                    nameColumn = field.getName().toUpperCase();
                }

                Class<?> typeParemetros = field.getType();

                if (typeParemetros.equals(String.class)) {
                    if (field.getAnnotation(Coluna.class).SIZE() > -1) {
                        typeColumn = "VARCHAR(" + field.getAnnotation(Coluna.class).SIZE() + ")";
                    } else {
                        typeColumn = "VARCHAR(100)";
                    }
                } else if (typeParemetros.equals(int.class)){
                    if (field.getAnnotation(Coluna.class).pk() == true) {
                        typeColumn = "INT NOT NULL";
                    } else {
                        typeColumn = "INT";
                    }
                } else if (typeParemetros.isEnum()) {
                    typeColumn = "INT";
                }

                if (i > 0) sb.append(",");

                sb.append("\n\t").append(nameColumn).append(" ").append(typeColumn);

            }

            sb.append(",\n\tPRIMARY KEY(");
            for (int y = 0; y < attributes.length; y++) {
                int get = 0;
                Field fields = attributes[y];

                if (fields.isAnnotationPresent(Coluna.class)) {
                    Coluna annotationColumn = fields.getAnnotation(Coluna.class);

                    if (annotationColumn.pk()) {
                        if (get > 0) sb.append(", ");

                        if (annotationColumn.nome().isEmpty()) {
                            sb.append(fields.getName().toUpperCase());
                        } else {
                            sb.append(annotationColumn.nome());
                        }
                        get++;
                    }
                }
                if (y == attributes.length - 1) {
                    sb.append(")");
                }
            }
            sb.append("\n);");

            String create = sb.toString();
            System.out.println(create);
            Statement execute = con.createStatement();
            execute.executeUpdate(create);

            return create;

        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDropTable(Connection con, Object obj) {
        try {
            String nameTable;
            StringBuilder sb = new StringBuilder();

            Class<?> cl = obj.getClass();

            if (cl.isAnnotationPresent(Tabela.class)) {
                Tabela table = cl.getAnnotation(Tabela.class);
                nameTable = table.value();
            } else {
                nameTable = cl.getSimpleName().toUpperCase();
            }

            sb.append("DROP TABLE ").append(nameTable).append(";");
            String drop = sb.toString();

            System.out.println(drop);
            Statement execute = con.createStatement();
            execute.executeUpdate(drop);
            return drop;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected PreparedStatement getSqlInsert(Connection con, Object obj) {
        Class<?> cl = obj.getClass();
        StringBuilder sb = new StringBuilder();
        String nameTable;

        if (cl.isAnnotationPresent(Tabela.class)) {
            Tabela table = cl.getAnnotation(Tabela.class);
            nameTable = table.value();
        } else {
            nameTable = cl.getSimpleName().toUpperCase();
        }

        sb.append("INSERT INTO ").append(nameTable).append(" (");

        Field[] attributes = cl.getDeclaredFields();

        for (int i = 0; i < attributes.length; i++) {
            Field field = attributes[i];
            String nameColumn;

            if (field.isAnnotationPresent(Coluna.class)) {
                Coluna column = field.getAnnotation(Coluna.class);
                if (column.nome().isEmpty()) {
                    nameColumn = field.getName().toUpperCase();
                } else {
                    nameColumn = column.nome();
                }
            } else {
                nameColumn = field.getName().toUpperCase();
            }

            if (i > 0) {
                sb.append(", ");
            }

            sb.append(nameColumn);
        }

        sb.append(") VALUES (");

        for (int i = 0; i < attributes.length; i++) {
            if (i > 0) sb.append(", ");

            sb.append("?");
        }
        sb.append(")");
        String insert = sb.toString();
        System.out.println(insert);

        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(insert);

            for (int i = 0; i < attributes.length; i++) {
                Field field = attributes[i];
                Object type = field.getType();

                field.setAccessible(true);
                if (type.equals(int.class)) {
                    ps.setInt(i + 1, field.getInt(obj));
                } else if (type.equals(String.class)) {
                    ps.setString(i + 1, String.valueOf(field.get(obj)));
                } else if (field.getType().isEnum()) {
                    Object value = field.get(obj);
                    Method m = value.getClass().getMethod("ordinal");
                    ps.setInt(i + 1, (Integer) m.invoke(value, null));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return ps;
    }

    @Override
    protected PreparedStatement getSqlSelectAll(Connection con, Object obj) {
        Class<?> cl = obj.getClass();
        StringBuilder sb = new StringBuilder();
        String nameTable;

        if (cl.isAnnotationPresent(Tabela.class)) {
            nameTable = cl.getAnnotation(Tabela.class).value();
        } else {
            nameTable = cl.getSimpleName().toUpperCase();
        }
        sb.append("SELECT * FROM ").append(nameTable).append(";");

        String select = sb.toString();
        System.out.println(select);
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(select);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ps;
    }

    @Override
    protected PreparedStatement getSqlSelectById(Connection con, Object obj, int id) {
        Class<?> cl = obj.getClass();
        StringBuilder sb = new StringBuilder();
        String nameTable;

        if (cl.isAnnotationPresent(Tabela.class)) {
            nameTable = cl.getAnnotation(Tabela.class).value();
        } else {
            nameTable = cl.getSimpleName().toUpperCase();
        }

        sb.append("SELECT * FROM ").append(nameTable).append(" WHERE ID=").append(id).append(";");
        String select = sb.toString();
        System.out.println(select);
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(select);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ps;
    }

    @Override
    protected PreparedStatement getSqlUpdateById(Connection con, Object obj, int id) {
        Class<?> cl = obj.getClass();
        StringBuilder sb = new StringBuilder();
        String nameTable;

        if (cl.isAnnotationPresent(Tabela.class)) {
            nameTable = cl.getAnnotation(Tabela.class).value();
        } else {
            nameTable = cl.getSimpleName().toUpperCase();
        }

        sb.append("UPDATE ").append(nameTable).append(" SET ");

        Field[] attributes = cl.getDeclaredFields();

        for (int i = 0; i < attributes.length; i++) {
            Field field = attributes[i];
            String nameColumn;

            if (field.isAnnotationPresent(Coluna.class)) {
                Coluna column = field.getAnnotation(Coluna.class);
                if (column.nome().isEmpty()) {
                    nameColumn = field.getName().toUpperCase();
                } else {
                    nameColumn = column.nome();
                }
            } else {
                nameColumn = field.getName().toUpperCase();
            }

            if (i > 0) {
                sb.append(", ");
            }

            sb.append(nameColumn).append(" = ?");
        }
        sb.append(" WHERE ID = ").append(id);
        String update = sb.toString();
        System.out.println(update);

        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(update);

            for (int i = 0; i < attributes.length; i++) {
                Field field = attributes[i];
                Object type = field.getType();

                field.setAccessible(true);
                if (type.equals(int.class)) {
                    ps.setInt(i + 1, field.getInt(obj));
                } else if (type.equals(String.class)) {
                    ps.setString(i + 1, String.valueOf(field.get(obj)));
                } else if (field.getType().isEnum()) {
                    Object value = field.get(obj);
                    Method m = value.getClass().getMethod("ordinal");
                    ps.setInt(i + 1, (Integer) m.invoke(value, null));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return ps;
    }

    @Override
    protected PreparedStatement getSqlDeleteById(Connection con, Object obj, int id) {
        PreparedStatement ps = null;
        try {
            Class<?> cl = obj.getClass();
            StringBuilder sb = new StringBuilder();
            String nameTable;

            if (cl.isAnnotationPresent(Tabela.class)) {
                nameTable = cl.getAnnotation(Tabela.class).value();
            } else {
                nameTable = cl.getSimpleName().toUpperCase();
            }

            sb.append("DELETE FROM ").append(nameTable).append(" WHERE ID = ").append(id).append(";");
            String delete = sb.toString();
            System.out.println(delete);

            ps = con.prepareStatement(delete);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ps;
    }

	@Override
	protected PreparedStatement getSqlSelectById(Connection con, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PreparedStatement getSqlUpdateById(Connection con, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PreparedStatement getSqlDeleteById(Connection con, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}
}