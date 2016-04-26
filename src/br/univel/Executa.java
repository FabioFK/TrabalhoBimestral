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
	protected PreparedStatement getSqlSelectById(Connection con, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}


}