steal(
		'jquery/controller',
		'jquery/view/ejs',
		'jwc/topic')
	.then(
		'jwc/models/api/v1/models/jmx_connection_model.js',
		'//jwc/widgets/dialogs/open_jmx_connection_dialog/views/init.ejs', function($){

// ----------------------------------------------------------------------------------------------------------
// controller
// ----------------------------------------------------------------------------------------------------------

/**
 * Open JMX Connection Dialog Widget.
 * 
 * Whenever a JMXConnection was successfully created (on the server) that JMXConnectionModel will be 
 * published to the topic 'opened.jmxconnection'.
 * 
 * @see jwc/topic
 * 
 */
$.Controller('jwc.widgets.dialogs.OpenJMXConnectionDialog',
/** @Static */
{
	defaults : {}
},
/** @Prototype */
{
	wDialogView : null,
	host : null,
	port : null,
	allFields : null,
	tips : null,
	
	/**
	 * Constructor
	 */
	init : function(){
		// init template
		this.element.html('//jwc/widgets/dialogs/open_jmx_connection_dialog/views/init.ejs', {});
		// set base elements
		this.wDialogView = this.element;
		this.host = $('#host', this.wDialogView);
		this.port = $('#port', this.wDialogView);
		this.allFields = $( [] ).add( host ).add( port );
		this.tips = $( ".validateTips", this.wDialogView);
		// initialize the dialog
		this.element.dialog({
			autoOpen: false,
			height: 300,
			width: 350,
			modal: true,
			buttons: {
				"Open Connection": this.proxy(function() {
					const self = this;
					var bValid = true;
					this.allFields.removeClass( "ui-state-error" );

					bValid = bValid && this._checkLength( this.host, "host", 3, 200 );
					bValid = bValid && this._checkLength( this.port, "port", 4, 5 );

					bValid = bValid && this._checkRegexp( this.port, /^[0-9]+$/i, "Port must be a number." );

					if ( bValid ) {
						new jwc.models.api.v1.JMXConnectionModel({host: this.host.val(), port: this.port.val()})
						.save(
							function(jmxConnectionModel){
								self.wDialogView.dialog( "close" );
								Topic.publish('opened.jmxconnection', jmxConnectionModel);
							},
							function(xmlHttpResponse){
								self._updateTips(	"Could not create connection: " + xmlHttpResponse.responseText);
							}
						) 
					}
				}),
				Cancel: this.proxy(function() {
					this.wDialogView.dialog( "close" );
				})
			},
			close: this.proxy(function() {
				this.allFields.val( "" ).removeClass( "ui-state-error" );
			})
		});
	},
	
	/**
	 * @hide
	 * 
	 * @param t
	 * @returns
	 */
	_updateTips : function ( t ) {
		const self = this;
		this.tips
			.text( t )
			.addClass( "ui-state-highlight" );
		setTimeout(function() {
			self.tips.removeClass( "ui-state-highlight", 1500 );
		}, 500 );
	},

	/**
	 * @hide
	 * 
	 * @param o
	 * @param n
	 * @param min
	 * @param max
	 * @returns
	 */
	_checkLength : function ( o, n, min, max ) {
		if ( o.val().length > max || o.val().length < min ) {
			o.addClass( "ui-state-error" );
			this._updateTips( "Length of " + n + " must be between " +
				min + " and " + max + "." );
			return false;
		} else {
			return true;
		}
	},

	/**
	 * @hide
	 * 
	 * @param o
	 * @param regexp
	 * @param n
	 * @returns
	 */
	_checkRegexp : function ( o, regexp, n ) {
		if ( !( regexp.test( o.val() ) ) ) {
			o.addClass( "ui-state-error" );
			this._updateTips( n );
			return false;
		} else {
			return true;
		}
	}

});

});