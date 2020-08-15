
// Simple tests

describe('rainbow', function () {
  describe('Rainbow(numberOfColors)', function () {
    it('should work with simple case', function () {
      var Rainbow = require('./');
      Rainbow.create(1)[0].rgbArray().should.eql([255, 0, 0]);
    });

    it('should work with semi-complex case', function () {
      var Rainbow = require('./');
      var rainbow = Rainbow.create(3);

      rainbow.length.should.equal(3);

      rainbow[0].rgbArray().should.eql([255, 0, 0]);
      rainbow[1].rgbArray().should.eql([0, 0, 255]);
      rainbow[2].rgbArray().should.eql([0, 255, 0]);
    });

    it('should work with complex case', function () {
      var Rainbow = require('./');
      var rainbow = Rainbow.create(12);

      rainbow.length.should.equal(12);

      rainbow[0].rgbArray().should.eql([255, 0, 0]);
      rainbow[1].rgbArray().should.eql([255, 0, 128]);
      rainbow[2].rgbArray().should.eql([255, 0, 255]);
      rainbow[3].rgbArray().should.eql([128, 0, 255]);
      rainbow[4].rgbArray().should.eql([0, 0, 255]);
      rainbow[5].rgbArray().should.eql([0, 128, 255]);
      rainbow[6].rgbArray().should.eql([0, 255, 255]);
      rainbow[7].rgbArray().should.eql([0, 255, 127]);
      rainbow[8].rgbArray().should.eql([0, 255, 0]);
      rainbow[9].rgbArray().should.eql([128, 255, 0]);
      rainbow[10].rgbArray().should.eql([255, 255, 0]);
      rainbow[11].rgbArray().should.eql([255, 128, 0]);
    });
  });

  describe('new Rainbow().next()', function () {
    it('should work with simple case', function () {
      var Rainbow = require('./');
      var rainbow = new Rainbow(3);

      rainbow.next().rgbArray().should.eql([255, 0, 0]);
      rainbow.next().rgbArray().should.eql([0, 0, 255]);
      rainbow.next().rgbArray().should.eql([0, 255, 0]);

      // Should loop
      rainbow.next().rgbArray().should.eql([255, 0, 0]);
      rainbow.next().rgbArray().should.eql([0, 0, 255]);
      rainbow.next().rgbArray().should.eql([0, 255, 0]);
    });

    it('should work with complex case', function () {
      var Rainbow = require('./');
      var rainbow = new Rainbow(12);

      rainbow.next().rgbArray().should.eql([255, 0, 0]);
      rainbow.next().rgbArray().should.eql([255, 0, 128]);
      rainbow.next().rgbArray().should.eql([255, 0, 255]);
      rainbow.next().rgbArray().should.eql([128, 0, 255]);
      rainbow.next().rgbArray().should.eql([0, 0, 255]);
      rainbow.next().rgbArray().should.eql([0, 128, 255]);
      rainbow.next().rgbArray().should.eql([0, 255, 255]);
      rainbow.next().rgbArray().should.eql([0, 255, 127]);
      rainbow.next().rgbArray().should.eql([0, 255, 0]);
      rainbow.next().rgbArray().should.eql([128, 255, 0]);
      rainbow.next().rgbArray().should.eql([255, 255, 0]);
      rainbow.next().rgbArray().should.eql([255, 128, 0]);
    });
  });
});