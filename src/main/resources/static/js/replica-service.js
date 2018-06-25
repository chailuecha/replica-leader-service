

var clusterMap = {};

$(document).ready( function () {
    $('#cluster-tabs').on("click", "a", function (e) {
        e.preventDefault();
        $(this).tab('show')
    })

    var addClusterPanel = new AddClusterPanel();
    renderAllClusters();
})

function alertSuccess(text) {
    $("#main-alert").removeClass().addClass("alert alert-success").html(text).show();
}

function alertError(text) {
    $("#main-alert").removeClass().addClass("alert alert-danger").html(text).show();
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
        var clusterName = this.clusterNameInput.val();
        var zkCons = this.zkConsInput.val();

        $.ajax({
            method: "PUT",
            url : "/clusters/" + clusterName,
            data : {"zkCons" : zkCons }

        }).done( function (response) {
            var cluster = new ClusterPanel(clusterName, zkCons);
            clusterMap[clusterName] = cluster;
            cluster.addPanel();

        }).fail( function (jqXHR, textStatus) {
            alertError( "ERROR : " + jqXHR.responseText)
        });
    }
}


function ClusterPanel(clusterName, zkCons) {
    var self = this;

    this.clusterTab = null;
    this.clusterPanel = null;

    this.addPanel = function () {
        this.clusterTab = $(this.newClusterTab());
        $('#cluster-tabs').append( this.clusterTab  );

        this.clusterPanel = $(this.newClusterPanel());
        $('#cluster-panels').append( this.clusterPanel );

        self.bindEvent();
    }

    this.newClusterTab = function () {
        var template = $('#cluster-tab-tpl').html();
        Mustache.parse(template);   // optional, speeds up future uses
        return Mustache.render(template, {tabId: clusterName , tabLabel : clusterName });
    }

    this.newClusterPanel = function () {
        var template = $('#cluster-panel-tpl').html();
        Mustache.parse(template);   // optional, speeds up future uses
        return Mustache.render(template, {tabId: clusterName,  tabName : clusterName,  zkCons : zkCons });
    }

    this.bindEvent = function() {
        this.clusterPanel.find("button.remove-cluster").click( function () {
            self.removeCluster();
            selectAddingClusterTab();
        })
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
}