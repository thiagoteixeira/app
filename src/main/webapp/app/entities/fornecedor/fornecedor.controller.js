(function() {
    'use strict';

    angular
        .module('appApp')
        .controller('FornecedorController', FornecedorController);

    FornecedorController.$inject = ['$scope', '$state', 'Fornecedor', 'FornecedorSearch'];

    function FornecedorController ($scope, $state, Fornecedor, FornecedorSearch) {
        var vm = this;
        
        vm.fornecedors = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Fornecedor.query(function(result) {
                vm.fornecedors = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            FornecedorSearch.query({query: vm.searchQuery}, function(result) {
                vm.fornecedors = result;
            });
        }    }
})();
