package de.metas.shipper.gateway.dpd.model;


/** Generated Interface for DPD_Shipper_Config
 *  @author Adempiere (generated) 
 */
@SuppressWarnings("javadoc")
public interface I_DPD_Shipper_Config 
{

    /** TableName=DPD_Shipper_Config */
    public static final String Table_Name = "DPD_Shipper_Config";

    /** AD_Table_ID=541433 */
//    public static final int Table_ID = org.compiere.model.MTable.getTable_ID(Table_Name);

//    org.compiere.util.KeyNamePair Model = new org.compiere.util.KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 7 - System - Client - Org
     */
//    java.math.BigDecimal accessLevel = java.math.BigDecimal.valueOf(7);

    /** Load Meta Data */

	/**
	 * Get Mandant.
	 * Mandant für diese Installation.
	 *
	 * <br>Type: TableDir
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public int getAD_Client_ID();

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/**
	 * Set Sektion.
	 * Organisatorische Einheit des Mandanten
	 *
	 * <br>Type: Search
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public void setAD_Org_ID (int AD_Org_ID);

	/**
	 * Get Sektion.
	 * Organisatorische Einheit des Mandanten
	 *
	 * <br>Type: Search
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public int getAD_Org_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/**
	 * Get Erstellt.
	 * Datum, an dem dieser Eintrag erstellt wurde
	 *
	 * <br>Type: DateTime
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public java.sql.Timestamp getCreated();

    /** Column definition for Created */
    public static final org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object> COLUMN_Created = new org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object>(I_DPD_Shipper_Config.class, "Created", null);
    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/**
	 * Get Erstellt durch.
	 * Nutzer, der diesen Eintrag erstellt hat
	 *
	 * <br>Type: Table
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public int getCreatedBy();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/**
	 * Set Delis ID.
	 *
	 * <br>Type: String
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public void setDelisID (java.lang.String DelisID);

	/**
	 * Get Delis ID.
	 *
	 * <br>Type: String
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public java.lang.String getDelisID();

    /** Column definition for DelisID */
    public static final org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object> COLUMN_DelisID = new org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object>(I_DPD_Shipper_Config.class, "DelisID", null);
    /** Column name DelisID */
    public static final String COLUMNNAME_DelisID = "DelisID";

	/**
	 * Set Delis Passwort.
	 *
	 * <br>Type: String
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public void setDelisPassword (java.lang.String DelisPassword);

	/**
	 * Get Delis Passwort.
	 *
	 * <br>Type: String
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public java.lang.String getDelisPassword();

    /** Column definition for DelisPassword */
    public static final org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object> COLUMN_DelisPassword = new org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object>(I_DPD_Shipper_Config.class, "DelisPassword", null);
    /** Column name DelisPassword */
    public static final String COLUMNNAME_DelisPassword = "DelisPassword";

	/**
	 * Set DPD Konfiguration.
	 *
	 * <br>Type: ID
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public void setDPD_Shipper_config_ID (int DPD_Shipper_config_ID);

	/**
	 * Get DPD Konfiguration.
	 *
	 * <br>Type: ID
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public int getDPD_Shipper_config_ID();

    /** Column definition for DPD_Shipper_config_ID */
    public static final org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object> COLUMN_DPD_Shipper_config_ID = new org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object>(I_DPD_Shipper_Config.class, "DPD_Shipper_config_ID", null);
    /** Column name DPD_Shipper_config_ID */
    public static final String COLUMNNAME_DPD_Shipper_config_ID = "DPD_Shipper_config_ID";

	/**
	 * Set Aktiv.
	 * Der Eintrag ist im System aktiv
	 *
	 * <br>Type: YesNo
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public void setIsActive (boolean IsActive);

	/**
	 * Get Aktiv.
	 * Der Eintrag ist im System aktiv
	 *
	 * <br>Type: YesNo
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public boolean isActive();

    /** Column definition for IsActive */
    public static final org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object> COLUMN_IsActive = new org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object>(I_DPD_Shipper_Config.class, "IsActive", null);
    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/**
	 * Set URL Api Login.
	 *
	 * <br>Type: String
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public void setLoginApiUrl (java.lang.String LoginApiUrl);

	/**
	 * Get URL Api Login.
	 *
	 * <br>Type: String
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public java.lang.String getLoginApiUrl();

    /** Column definition for LoginApiUrl */
    public static final org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object> COLUMN_LoginApiUrl = new org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object>(I_DPD_Shipper_Config.class, "LoginApiUrl", null);
    /** Column name LoginApiUrl */
    public static final String COLUMNNAME_LoginApiUrl = "LoginApiUrl";

	/**
	 * Set Lieferweg.
	 * Methode oder Art der Warenlieferung
	 *
	 * <br>Type: TableDir
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public void setM_Shipper_ID (int M_Shipper_ID);

	/**
	 * Get Lieferweg.
	 * Methode oder Art der Warenlieferung
	 *
	 * <br>Type: TableDir
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public int getM_Shipper_ID();

	public org.compiere.model.I_M_Shipper getM_Shipper();

	public void setM_Shipper(org.compiere.model.I_M_Shipper M_Shipper);

    /** Column definition for M_Shipper_ID */
    public static final org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, org.compiere.model.I_M_Shipper> COLUMN_M_Shipper_ID = new org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, org.compiere.model.I_M_Shipper>(I_DPD_Shipper_Config.class, "M_Shipper_ID", org.compiere.model.I_M_Shipper.class);
    /** Column name M_Shipper_ID */
    public static final String COLUMNNAME_M_Shipper_ID = "M_Shipper_ID";

	/**
	 * Set Papierformat.
	 *
	 * <br>Type: List
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public void setPaperFormat (java.lang.String PaperFormat);

	/**
	 * Get Papierformat.
	 *
	 * <br>Type: List
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public java.lang.String getPaperFormat();

    /** Column definition for PaperFormat */
    public static final org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object> COLUMN_PaperFormat = new org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object>(I_DPD_Shipper_Config.class, "PaperFormat", null);
    /** Column name PaperFormat */
    public static final String COLUMNNAME_PaperFormat = "PaperFormat";

	/**
	 * Set URL Api Shipment Service.
	 *
	 * <br>Type: String
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public void setShipmentServiceApiUrl (java.lang.String ShipmentServiceApiUrl);

	/**
	 * Get URL Api Shipment Service.
	 *
	 * <br>Type: String
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public java.lang.String getShipmentServiceApiUrl();

    /** Column definition for ShipmentServiceApiUrl */
    public static final org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object> COLUMN_ShipmentServiceApiUrl = new org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object>(I_DPD_Shipper_Config.class, "ShipmentServiceApiUrl", null);
    /** Column name ShipmentServiceApiUrl */
    public static final String COLUMNNAME_ShipmentServiceApiUrl = "ShipmentServiceApiUrl";

	/**
	 * Set Shipper Product.
	 *
	 * <br>Type: String
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public void setShipperProduct (java.lang.String ShipperProduct);

	/**
	 * Get Shipper Product.
	 *
	 * <br>Type: String
	 * <br>Mandatory: false
	 * <br>Virtual Column: false
	 */
	public java.lang.String getShipperProduct();

    /** Column definition for ShipperProduct */
    public static final org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object> COLUMN_ShipperProduct = new org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object>(I_DPD_Shipper_Config.class, "ShipperProduct", null);
    /** Column name ShipperProduct */
    public static final String COLUMNNAME_ShipperProduct = "ShipperProduct";

	/**
	 * Get Aktualisiert.
	 * Datum, an dem dieser Eintrag aktualisiert wurde
	 *
	 * <br>Type: DateTime
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public java.sql.Timestamp getUpdated();

    /** Column definition for Updated */
    public static final org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object> COLUMN_Updated = new org.adempiere.model.ModelColumn<I_DPD_Shipper_Config, Object>(I_DPD_Shipper_Config.class, "Updated", null);
    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/**
	 * Get Aktualisiert durch.
	 * Nutzer, der diesen Eintrag aktualisiert hat
	 *
	 * <br>Type: Table
	 * <br>Mandatory: true
	 * <br>Virtual Column: false
	 */
	public int getUpdatedBy();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";
}
