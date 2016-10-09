package nc.noumea.spring.service;

/*
 * #%L
 * Contrats Particuliers
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 Mairie de Nouméa
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.awt.Color;

import com.lowagie.text.Font;

public class CellVo {

	private String	text;
	private boolean	isBold;
	private Integer	colspan;
	private Color	backgroundColor;
	private Integer	horizontalAlign;
	private boolean	isBorder;

	private Font	font;

	/**
	 * @param text texte a afficher
	 * @see Par defaut : - text non gras (normal) - pas de couleur en fond (backgroundColor = null) - colspan = 1 - aligne a gauche - avec bordure
	 */
	public CellVo(String text) {
		this(text, false);
	}

	/**
	 * @param text texte a afficher
	 * @param font font police
	 * @see Par defaut : - text non gras (normal) - pas de couleur en fond (backgroundColor = null) - colspan = 1 - aligne a gauche - avec bordure
	 */
	public CellVo(String text, Font font) {
		this(text, false, 1, null, null, true, font);
	}

	/**
	 * @param text texte a afficher
	 * @param isBold texte en gras ou non
	 * @see Par defaut : - pas de couleur en fond (backgroundColor = null) - colspan = 1 - aligne a gauche - avec bordure
	 */
	public CellVo(String text, boolean isBold) {
		this(text, isBold, 1, null, null);
	}

	/**
	 * 
	 * @param text texte a afficher
	 * @param colspan colspan pour les colonnes d un tableau
	 * @see Par defaut : - text non gras (normal) - pas de couleur en fond (backgroundColor = null) - aligne a gauche - avec bordure
	 */
	public CellVo(String text, Integer colspan) {
		this(text, false, colspan, null, null);
	}

	/**
	 * 
	 * @param text texte a afficher
	 * @param colspan colspan pour les colonnes d un tableau
	 * @param horizontalAlign alignement horizontal du text
	 * @see Par defaut : - text non gras (normal) - pas de couleur en fond (backgroundColor = null) - avec bordure
	 */
	public CellVo(String text, Integer colspan, Integer horizontalAlign) {
		this(text, false, colspan, null, horizontalAlign);
	}

	/**
	 * 
	 * @param text texte a afficher
	 * @param backgroundColor couleur en fond
	 * @see Par defaut : - text non gras (normal) - colspan = 1 - aligne a gauche - avec bordure
	 */
	public CellVo(String text, Color backgroundColor) {
		this(text, false, 1, backgroundColor, null);
	}

	/**
	 * 
	 * @param text texte a afficher
	 * @param isBold texte en gras ou non
	 * @param colspan colspan pour les colonnes d un tableau
	 * @param backgroundColor couleur en fond
	 * @see Par defaut : - aligne a gauche - avec bordure
	 */
	public CellVo(String text, boolean isBold, Integer colspan, Color backgroundColor) {
		this(text, isBold, colspan, backgroundColor, null);
	}

	/**
	 * 
	 * @param text texte a afficher
	 * @param isBold texte en gras ou non
	 * @param colspan colspan pour les colonnes d un tableau
	 * @param backgroundColor couleur en fond
	 * @param horizontalAlign alignement horizontal du text
	 * @see Par defaut : - avec bordure
	 */
	public CellVo(String text, boolean isBold, Integer colspan, Color backgroundColor, Integer horizontalAlign) {
		this(text, isBold, colspan, backgroundColor, horizontalAlign, true);
	}

	/**
	 * 
	 * @param text texte a afficher
	 * @param isBold texte en gras ou non
	 * @param colspan colspan pour les colonnes d un tableau
	 * @param backgroundColor couleur en fond
	 * @param horizontalAlign alignement horizontal du text
	 * @param isBorder bordure ou non d une case d un tableau
	 */
	public CellVo(String text, boolean isBold, Integer colspan, Color backgroundColor, Integer horizontalAlign, boolean isBorder) {
		super();
		this.text = text;
		this.isBold = isBold;
		this.colspan = colspan;
		this.backgroundColor = backgroundColor;
		this.horizontalAlign = horizontalAlign;
		this.isBorder = isBorder;
	}

	/**
	 * 
	 * @param text texte a afficher
	 * @param isBold texte en gras ou non
	 * @param colspan colspan pour les colonnes d un tableau
	 * @param backgroundColor couleur en fond
	 * @param horizontalAlign alignement horizontal du text
	 * @param isBorder bordure ou non d une case d un tableau
	 */
	public CellVo(String text, boolean isBold, Integer colspan, Color backgroundColor, Integer horizontalAlign, boolean isBorder, Font font) {
		super();
		this.text = text;
		this.isBold = isBold;
		this.colspan = colspan;
		this.backgroundColor = backgroundColor;
		this.horizontalAlign = horizontalAlign;
		this.isBorder = isBorder;
		this.font = font;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isBold() {
		return isBold;
	}

	public void setBold(boolean isBold) {
		this.isBold = isBold;
	}

	public Integer getColspan() {
		return colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Integer getHorizontalAlign() {
		return horizontalAlign;
	}

	public void setHorizontalAlign(Integer horizontalAlign) {
		this.horizontalAlign = horizontalAlign;
	}

	public boolean isBorder() {
		return isBorder;
	}

	public void setBorder(boolean isBorder) {
		this.isBorder = isBorder;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

}
