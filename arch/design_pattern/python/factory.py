#!/usr/bin/env python
# encoding: utf-8

class DataDecoder(object):
  """An abstract class which is used to decode data for a provider."""

  __metaclass__ = abc.ABCMeta

  @abc.abstractmethod
  def decode(self, data, items):
    pass

  @abc.abstractmethod
  def list_items(self):
    pass
