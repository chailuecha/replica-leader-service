

var clusterMap = {};
var addClusterPanel = new AddClusterPanel();

$(document).ready( function () {
    $('#cluster-tabs').on("click", "a", function (e) {
        clearAlert();
        e.preventDefault();
        $(this).tab('show')
    })
    
    renderAllClusters();
})

function alertSuccess(text) {
    $("#main-alert").removeClass().addClass("alert alert-success").html(text).show();
}

function alertError(text) {
    $("#main-alert").removeClass().addClass("alert alert-danger").html(text).show();
}

function clearAlert() {
    $("#main-alert").removeClass().html("").hide();
}

function selectAddingClusterTab() {
    $('#cluster-tabs').find('a[href="#add-cluster"]').click();
}

function renderAllClusters() {
    $.ajax({
        method : "GET",
        url : "/clusters/all",
        dataType : "json"

    }).done(function (clusterList) {
        for(var i=0; i<clusterList.length; i++){
            var clusterInfo = clusterList[i];
            var cluster = new ClusterPanel(clusterInfo.name, clusterInfo.zkCons);

            clusterMap[clusterInfo.name] = cluster;
            cluster.addPanel();
        }


    }).fail( function (jqXHR, textStatus) {
        alertError( "ERROR : " + jqXHR.responseText)

    });
}

function AddClusterPanel() {
    var self = this;
    this.clusterNameInput = $("#cluster-name");
    this.zkConsInput = $("#zk-cons");

    $("#add-cluster-bn").click( function () {
        self.addCluster();
    });

    this.addCluster = function () {
        clearAlert();

        var clusterName = $.trim( this.clusterNameInput.val() );
        var zkCons = $.trim( this.zkConsInput.val() );

        this.clusterNameInput.val(clusterName)
        this.zkConsInput.val(zkCons)

        if( clusterMap[clusterName] !== undefined){
            alertError("Cluster " + clusterName + " is already existed")
            return
        }

        $.ajax({
            method: "PUT",
            url : "/clusters/" + clusterName,
            data : {"zkCons" : zkCons }

        }).done( function (response) {
            var cluster = new ClusterPanel(clusterName, zkCons);
            clusterMap[clusterName] = cluster;

            cluster.addPanel();
            cluster.selectCluster();
            self.clearInput();

        }).fail( function (jqXHR, textStatus) {
            alertError( "ERROR : " + jqXHR.responseText)
        })
    }
    
    this.clearInput = function () {
        this.clusterNameInput.val("");
        this.zkConsInput.val("");
    }
}



function ClusterPanel(clusterName, zkCons) {
    var self = this;

    var PanelStatus = {
        //all these strange boolean just because Mustache doesn't play well with logic expression in templating
        INIT : { statusInit : true},
        STARTED : { statusStarted : true},
        PAUSED : { statusPaused : true}
    }

    this.clusterTabHeader = null;
    this.clusterPanel = null;

    this.status = PanelStatus.INIT;
    this.actionBnGroup = null;
    this.startBn = null;
    this.stopBn = null;
    this.pauseBn = null;



    this.runInterval = null;
    this.runIntervalUnit = null;

    this.removeClusterBn = null;

    this.addPanel = function () {
        this.clusterTabHeader = $(this.newClusterTabHeader());
        $('#cluster-tabs').append( this.clusterTabHeader  );

        this.clusterPanel = $(this.newClusterPanel());

        this.actionBnGroup = this.clusterPanel.find("div.action-bn-group");
        this.startBn = this.clusterPanel.find("button[name='start']");
        this.stopBn = this.clusterPanel.find("button[name='stop']");
        this.pauseBn = this.clusterPanel.find("button[name='pause']");
        this.renderActionBn();

        this.runInterval = this.clusterPanel.find("input[name='runInterval']");
        this.runIntervalUnit = this.clusterPanel.find("select[name='runIntervalUnit']");

        this.removeClusterBn = this.clusterPanel.find("button.remove-cluster");
        this.clusterPanel.find(".selectpicker").selectpicker("render");
        $('#cluster-panels').append( this.clusterPanel );

        self.bindEvent();
    }

    this.renderActionBn = function () {
        var template = $('#cluster-action-bn-tpl').html();
        Mustache.parse(template);   // optional, speeds up future uses

        var bnGroup = Mustache.render(template, this.status );
        this.actionBnGroup.empty().append(bnGroup);
    }

    
    this.newClusterTabHeader = function () {
        var template = $('#cluster-tab-tpl').html();
        Mustache.parse(template);   // optional, speeds up future uses
        return Mustache.render(template, {tabId: clusterName , tabLabel : clusterName });
    }

    this.newClusterPanel = function () {
        var template = $('#cluster-panel-tpl').html();
        Mustache.parse(template);   // optional, speeds up future uses
        return Mustache.render(template, {tabId: clusterName,  tabName : clusterName,  zkCons : zkCons });
    }

    this.selectCluster = function () {
        this.clusterTabHeader.find('a[role="tab"]').click();
    }
    
    this.bindEvent = function() {
        this.removeClusterBn.click( function () {
            clearAlert();
            self.removeCluster();
            selectAddingClusterTab();
        })
        
        this.actionBnGroup.on("click", "button", function () {
            clearAlert();

            var action = $(this).html();

            if ( action == "Start" ){
                self.startService();
                self.disableInput(true);
                self.status = PanelStatus.STARTED;
                self.renderActionBn();

            }else if ( action == "Stop" ){
                self.stopService();
                self.disableInput(false);
                self.status = PanelStatus.INIT;
                self.renderActionBn();

            }else if ( action == "Pause" ){
                self.pauseService();
                self.status = PanelStatus.PAUSED;
                self.renderActionBn();

            }else if ( action == "Resume" ){
                self.resumeService();
                self.status = PanelStatus.STARTED;
                self.renderActionBn();

            }else{
                alertError( "ERROR : Invalid cluster action : " + action);
            }
        });


    }

    this.disableInput = function (disable) {
        this.clusterPanel.find("input").prop('disabled', disable);
        this.clusterPanel.find("select").prop('disabled', disable);
    }

    this.removeCluster = function () {
        $.ajax({
            method : "DELETE",
            url : "/clusters/" + clusterName

        }).done(function () {
            self.clusterTab.remove();
            self.clusterPanel.remove();

            delete clusterMap[clusterName];

        }).fail(function (jqXHR, textStatus) {
            alertError( "ERROR : " + jqXHR.responseText)
        });
    }

    this.startService = function () {

    }

    this.stopService = function () {

    }

    this.pauseService = function () {

    }

    this.resumeService = function () {

    }
}
