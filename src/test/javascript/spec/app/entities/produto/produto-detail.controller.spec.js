'use strict';

describe('Controller Tests', function() {

    describe('Produto Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockProduto, MockFornecedor;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockProduto = jasmine.createSpy('MockProduto');
            MockFornecedor = jasmine.createSpy('MockFornecedor');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Produto': MockProduto,
                'Fornecedor': MockFornecedor
            };
            createController = function() {
                $injector.get('$controller')("ProdutoDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'appApp:produtoUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
