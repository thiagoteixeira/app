(function() {
    'use strict';

    angular
        .module('appApp')
        .factory('FornecedorSearch', FornecedorSearch);

    FornecedorSearch.$inject = ['$resource'];

    function FornecedorSearch($resource) {
        var resourceUrl =  'api/_search/fornecedors/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
