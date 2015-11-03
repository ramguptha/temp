How to Create an Advanced Filter Programmatically
=================================================

Sometimes, you need to create OData queries with the proper URI without the advanced query UI.

Here is the example for a proper structure of the advanced filter with 2 OR conditions:

      var advanced = Query.Advanced.create({
        filter:Query.Advanced.AndFilter.create({
          params: Em.A([ Query.Advanced.OrFilter.create({
            params: Em.A([ Query.Advanced.FilterOpWrapper.create({
              filterAttr: 'foo',
              filterOp: Query.Advanced.EqualsOp.create({
                rval: 'bar'
              })
            })
            ])
          }), Query.Advanced.OrFilter.create({
            params: Em.A([ Query.Advanced.FilterOpWrapper.create({
              filterAttr: 'booga',
              filterOp: Query.Advanced.SubstringOfOp.create({
                rval: 'tika'
              })
            }), Query.Advanced.FilterOpWrapper.create({
              filterAttr: 'version',
              filterOp: Query.Advanced.EqualsOp.create({
                rval: '1.0'
              })
            })
            ])
          })
          ])
        })
      });

In case you want 2 AND conditions you can do something like this:

      var searchFilter = AdvancedFilter.AndFilter.create({
        params: Em.A([ AdvancedFilter.FilterOpWrapper.create({
          filterAttr: 'titlingDbId',
          filterAttrType: String,
          filterOp: AdvancedFilter.EqualsOp.create({
            rval: this.get('id').toString()
          })
        }),
          AdvancedFilter.FilterOpWrapper.create({
            filterAttr: 'definition',
            filterAttrType: String,
            filterOp: AdvancedFilter.NotEqualsOp.create({
              rval: 'null'
            })
          })
        ])
      });

      this.get('relatedFingerprintsController.query').set('contextFilter', searchFilter);
