package com.biblioteca.persistence;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Objects;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/**
 * Usa JDBC 4.2 diretamente, sem converter LocalDate para java.sql.Date.
 * A conversão legada altera datas históricas em fusos com horário solar local.
 * Mantém a coluna SQL DATE e funciona com H2 e com o driver PostgreSQL.
 * Referência: https://h2database.com/html/datatypes.html#date_type
 */
public class LocalDateType implements UserType<LocalDate> {
    @Override
    public int getSqlType() { return Types.DATE; }

    @Override
    public Class<LocalDate> returnedClass() { return LocalDate.class; }

    @Override
    public boolean equals(LocalDate first, LocalDate second) { return Objects.equals(first, second); }

    @Override
    public int hashCode(LocalDate value) { return Objects.hashCode(value); }

    @Override
    public LocalDate nullSafeGet(ResultSet result, int position,
            SharedSessionContractImplementor session, Object owner) throws SQLException {
        return result.getObject(position, LocalDate.class);
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, LocalDate value, int index,
            SharedSessionContractImplementor session) throws SQLException {
        if (value == null) statement.setNull(index, Types.DATE);
        else statement.setObject(index, value, Types.DATE);
    }

    @Override
    public boolean isMutable() { return false; }

    @Override
    public LocalDate deepCopy(LocalDate value) { return value; }

    @Override
    public Serializable disassemble(LocalDate value) { return value; }

    @Override
    public LocalDate assemble(Serializable cached, Object owner) { return (LocalDate) cached; }
}
