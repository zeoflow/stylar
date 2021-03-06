package com.zeoflow.stylar.ext.tables;

import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.stylar.Stylar;

import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TableCell;
import org.commonmark.ext.gfm.tables.TableHead;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.CustomNode;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to parse <code>TableBlock</code> and return a data-structure that is not dependent
 * on commonmark-java table extension. Can be useful when rendering tables require special
 * handling (multiple views, specific table view) for example when used with `stylar-recycler` artifact
 *
 * @see #parse(Stylar, TableBlock)
 * @since 3.0.0
 */
public class Table
{

    private final List<Row> rows;

    public Table(@NonNull List<Row> rows)
    {
        this.rows = rows;
    }

    /**
     * Factory method to obtain an instance of {@link Table}
     *
     * @param stylar     Markwon
     * @param tableBlock TableBlock to parse
     * @return parsed {@link Table} or null
     */
    @Nullable
    public static Table parse(@NonNull Stylar stylar, @NonNull TableBlock tableBlock)
    {

        final Table table;

        final ParseVisitor visitor = new ParseVisitor(stylar);
        tableBlock.accept(visitor);
        final List<Row> rows = visitor.rows();

        if (rows == null)
        {
            table = null;
        } else
        {
            table = new Table(rows);
        }

        return table;
    }

    @NonNull
    public List<Row> rows()
    {
        return rows;
    }

    @Override
    public String toString()
    {
        return "Table{" +
            "rows=" + rows +
            '}';
    }

    public enum Alignment
    {
        LEFT,
        CENTER,
        RIGHT
    }

    public static class Row
    {

        private final boolean isHeader;
        private final List<Column> columns;

        public Row(
            boolean isHeader,
            @NonNull List<Column> columns)
        {
            this.isHeader = isHeader;
            this.columns = columns;
        }

        public boolean header()
        {
            return isHeader;
        }

        @NonNull
        public List<Column> columns()
        {
            return columns;
        }

        @Override
        public String toString()
        {
            return "Row{" +
                "isHeader=" + isHeader +
                ", columns=" + columns +
                '}';
        }
    }

    public static class Column
    {

        private final Alignment alignment;
        private final Spanned content;

        public Column(@NonNull Alignment alignment, @NonNull Spanned content)
        {
            this.alignment = alignment;
            this.content = content;
        }

        @NonNull
        public Alignment alignment()
        {
            return alignment;
        }

        @NonNull
        public Spanned content()
        {
            return content;
        }

        @Override
        public String toString()
        {
            return "Column{" +
                "alignment=" + alignment +
                ", content=" + content +
                '}';
        }
    }

    static class ParseVisitor extends AbstractVisitor
    {

        private final Stylar stylar;

        private List<Row> rows;

        private List<Column> pendingRow;
        private boolean pendingRowIsHeader;

        ParseVisitor(@NonNull Stylar stylar)
        {
            this.stylar = stylar;
        }

        @NonNull
        private static Table.Alignment alignment(@NonNull TableCell.Alignment alignment)
        {
            final Table.Alignment out;
            if (TableCell.Alignment.RIGHT == alignment)
            {
                out = Table.Alignment.RIGHT;
            } else if (TableCell.Alignment.CENTER == alignment)
            {
                out = Table.Alignment.CENTER;
            } else
            {
                out = Table.Alignment.LEFT;
            }
            return out;
        }

        @Nullable
        public List<Row> rows()
        {
            return rows;
        }

        @Override
        public void visit(CustomNode customNode)
        {

            if (customNode instanceof TableCell)
            {

                final TableCell cell = (TableCell) customNode;

                if (pendingRow == null)
                {
                    pendingRow = new ArrayList<>(2);
                }

                pendingRow.add(new Table.Column(alignment(cell.getAlignment()), stylar.render(cell)));
                pendingRowIsHeader = cell.isHeader();

                return;
            }

            if (customNode instanceof TableHead
                || customNode instanceof TableRow)
            {

                visitChildren(customNode);

                // this can happen, ignore such row
                if (pendingRow != null && pendingRow.size() > 0)
                {

                    if (rows == null)
                    {
                        rows = new ArrayList<>(2);
                    }

                    rows.add(new Table.Row(pendingRowIsHeader, pendingRow));
                }

                pendingRow = null;
                pendingRowIsHeader = false;

                return;
            }

            visitChildren(customNode);
        }
    }
}
