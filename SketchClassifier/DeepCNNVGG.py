"""
More in-depth examples and reproductions of paper results are maintained in
a separate repository: https://github.com/Lasagne/Recipes
"""

from __future__ import print_function

import zerorpc
import sys
import os
import time

import numpy as np
from skimage import io
import scipy.misc as sp
from skimage import color
import theano
import theano.tensor as T
from image import ImageDataGenerator

import lasagne


# ################## Download and prepare the MNIST dataset ##################
# This is just some way of getting the MNIST dataset from an online location
# and loading it into numpy arrays. It doesn't involve Lasagne at all.

num_categories = 10

batch_size = 50

categories = ['airplane',    'alarm clock',    'angel',    'ant',    'apple',    'arm',    'armchair',    'ashtray',    'axe',    'backpack',    'banana',    'barn',    'baseball bat',    'basket',    'bathtub',    'bear (animal)',    'bed',    'bee',    'beer-mug',    'bell',    'bench',    'bicycle',    'binoculars',    'blimp',    'book',    'bookshelf',    'boomerang',    'bottle opener',    'bowl',    'brain',    'bread',    'bridge',    'bulldozer',    'bus',    'bush',    'butterfly',    'cabinet',    'cactus',    'cake',    'calculator',    'camel',    'camera',    'candle',    'cannon',    'canoe',    'car (sedan)',    'carrot',    'castle',    'cat',    'cell phone',    'chair',    'chandelier',    'church',    'cigarette',    'cloud',    'comb',    'computer monitor',    'computer-mouse',    'couch',    'cow',    'crab',    'crane (machine)',    'crocodile',    'crown',    'cup',    'diamond',    'dog',    'dolphin',    'donut',    'door',    'door handle',    'dragon',    'duck',    'ear',    'elephant',    'envelope',    'eye',    'eyeglasses',    'face',    'fan',    'feather',    'fire hydrant',    'fish',    'flashlight',    'floor lamp',    'flower with stem',    'flying bird',    'flying saucer',    'foot',    'fork',    'frog',    'frying-pan',    'giraffe',    'grapes',    'grenade',    'guitar',    'hamburger',    'hammer',    'hand',    'harp',    'hat',    'head',    'head-phones',    'hedgehog',    'helicopter',    'helmet',    'horse',    'hot air balloon',    'hot-dog',    'hourglass',    'house',    'human-skeleton',    'ice-cream-cone',    'ipod',    'kangaroo',    'key',    'keyboard',    'knife',    'ladder',    'laptop',    'leaf',    'lightbulb',    'lighter',    'lion',    'lobster',    'loudspeaker',    'mailbox',    'megaphone',    'mermaid',    'microphone',    'microscope',    'monkey',    'moon',    'mosquito',    'motorbike',    'mouse (animal)',    'mouth',    'mug',    'mushroom',    'nose',    'octopus',    'owl',    'palm tree',    'panda',    'paper clip',    'parachute',    'parking meter',    'parrot',    'pear',    'pen',    'penguin',    'person sitting',    'person walking',    'piano',    'pickup truck',    'pig',    'pigeon',    'pineapple',    'pipe (for smoking)',    'pizza',    'potted plant',    'power outlet',    'present',    'pretzel',    'pumpkin',    'purse',    'rabbit',    'race car',    'radio',    'rainbow',    'revolver',    'rifle',    'rollerblades',    'rooster',    'sailboat',    'santa claus',    'satellite',    'satellite dish',    'saxophone',    'scissors',    'scorpion',    'screwdriver',    'sea turtle',    'seagull',    'shark',    'sheep',    'ship',    'shoe',    'shovel',    'skateboard',    'skull',    'skyscraper',    'snail',    'snake',    'snowboard',    'snowman',    'socks',    'space shuttle',    'speed-boat',    'spider',    'sponge bob',    'spoon',    'squirrel',    'standing bird',    'stapler',    'strawberry',    'streetlight',    'submarine',    'suitcase',    'sun',    'suv',    'swan',    'sword',    'syringe',    't-shirt',    'table',    'tablelamp',    'teacup',    'teapot',    'teddy-bear',    'telephone',    'tennis-racket',    'tent',    'tiger',    'tire',    'toilet',    'tomato',    'tooth',    'toothbrush',    'tractor',    'traffic light',    'train',    'tree',    'trombone',    'trousers',    'truck',    'trumpet',    'tv',    'umbrella',    'van',    'vase',    'violin',    'walkie talkie',    'wheel',    'wheelbarrow',    'windmill',    'wine-bottle',    'wineglass',    'wrist-watch',    'zebra']

datagen = ImageDataGenerator(
        featurewise_center=True,  # set input mean to 0 over the dataset
        samplewise_center=True,  # set each sample mean to 0
        featurewise_std_normalization=True,  # divide inputs by std of the dataset
        samplewise_std_normalization=False,  # divide each input by its std
        zca_whitening=False,  # apply ZCA whitening
        rotation_range=20,  # randomly rotate images in the range (degrees, 0 to 180)
        width_shift_range=0.2,  # randomly shift images horizontally (fraction of total width)
        height_shift_range=0.2,  # randomly shift images vertically (fraction of total height)
        horizontal_flip=True,  # randomly flip images
        vertical_flip=False)  # randomly flip images


def load_dataset_old():
    # We first define some helper functions for supporting both Python 2 and 3.
    if sys.version_info[0] == 2:
        from urllib import urlretrieve
        import cPickle as pickle

        def pickle_load(f, encoding):
            return pickle.load(f)
    else:
        from urllib.request import urlretrieve
        import pickle

        def pickle_load(f, encoding):
            return pickle.load(f, encoding=encoding)

    # We'll now download the MNIST dataset if it is not yet available.
    url = 'http://deeplearning.net/data/mnist/mnist.pkl.gz'
    filename = 'mnist.pkl.gz'
    if not os.path.exists(filename):
        print("Downloading MNIST dataset...")
        urlretrieve(url, filename)

    # We'll then load and unpickle the file.
    import gzip
    with gzip.open(filename, 'rb') as f:
        data = pickle_load(f, encoding='latin-1')

    # The MNIST dataset we have here consists of six numpy arrays:
    # Inputs and targets for the training set, validation set and test set.
    X_train, y_train = data[0]
    print(X_train)
    print(y_train)
    X_val, y_val = data[1]
    X_test, y_test = data[2]

    # The inputs come as vectors, we reshape them to monochrome 2D images,
    # according to the shape convention: (examples, channels, rows, columns)
    X_train = X_train.reshape((-1, 1, 28, 28))
    X_val = X_val.reshape((-1, 1, 28, 28))
    X_test = X_test.reshape((-1, 1, 28, 28))

    # The targets are int64, we cast them to int8 for GPU compatibility.
    y_train = y_train.astype(np.uint8)
    y_val = y_val.astype(np.uint8)
    y_test = y_test.astype(np.uint8)

    # We just return all the arrays in order, as expected in main().
    # (It doesn't matter how we do this as long as we can read them again.)
    return X_train, y_train, X_val, y_val, X_test, y_test


def load_dataset():

    from skimage import transform
    data = []
    data_test = []
    y_train = []
    y_test = []
    i = 1
    fold = 0.1*80
    for cat in range(0, num_categories): #len(categories) # a model was save with 20 classes

        for k in range(0, 80):
            img = io.imread('data/png/' + str(categories[cat]) + '/' + str(i) + '.png',as_grey=True)

            #extract HOG descriptor
            #img = color.rgb2gray(img)

       #     fd, hog_image = hog(img, orientations=8, pixels_per_cell=(16, 16), cells_per_block=(1, 1), visualise=True)

       #     img = exposure.rescale_intensity(hog_image, in_range=(0, 0.02))
           # imgflip = np.fliplr(img)

            img = sp.imresize(img, (224, 224)) # resize accoriding to model
            img = img.flatten() #reshape((9216))
            img = np.abs(img - np.mean(img))
            if k < 80 - fold:
                data.append(img)
                y_train.append(cat)
            else:
                data_test.append(img)
                y_test.append(cat)

            '''
            Experimental Congealing and Data Augmentation for closer to having One-Shot Learning

            img2 = imgflip.reshape((9216))
            data.append(img2)
            y_train.append(cat)
            '''

            i += 1

    data = np.array(data)
    # The MNIST dataset we have here consists of six numpy arrays:
    # Inputs and targets for the training set, validation set and test set.
    #X_train, y_train = data[0]


    X_train = data
    y_train = np.array(y_train)

    X_val = np.array(data_test)
    y_val = np.array(y_test)
    X_test = X_train[0:fold]
    y_test = y_train[0:fold]
    # X_val, y_val = data[1]
    # X_test, y_test = data[2]

    # The inputs come as vectors, we reshape them to monochrome 2D images,
    # according to the shape convention: (examples, channels, rows, columns)
    X_train = X_train.reshape((-1, 1, 224, 224))
    X_val = X_val.reshape((-1, 1, 224, 224))
    X_test = X_test.reshape((-1, 1, 224, 224))

    # The targets are int64, we cast them to int8 for GPU compatibility.
    y_train = y_train.astype(np.uint8)
    y_val = y_val.astype(np.uint8)
    y_test = y_test.astype(np.uint8)

    # We just return all the arrays in order, as expected in main().
    # (It doesn't matter how we do this as long as we can read them again.)
    return X_train, y_train, X_val, y_val, X_test, y_test

# ##################### Build the neural network model #######################
# This script supports three types of models. For each one, we define a
# function that takes a Theano variable representing the input and returns
# the output layer of a neural network model build in Lasagne.

def build_mlp(input_var=None):
    # This creates an MLP of two hidden layers of 800 units each, followed by
    # a softmax output layer of 10 units. It applies 20% dropout to the input
    # data and 50% dropout to the hidden layers.

    # Input layer, specifying the expected input shape of the network
    # (unspecified batchsize, 1 channel, 28 rows and 28 columns) and
    # linking it to the given Theano variable `input_var`, if any:
    l_in = lasagne.layers.InputLayer(shape=(None, 1, 224, 224),
                                     input_var=input_var)

    # Apply 20% dropout to the input data:
    l_in_drop = lasagne.layers.DropoutLayer(l_in, p=0.2)

    # Add a fully-connected layer of 800 units, using the linear rectifier, and
    # initializing weights with Glorot's scheme (which is the default anyway):
    l_hid1 = lasagne.layers.DenseLayer(
            l_in_drop, num_units=800,
            nonlinearity=lasagne.nonlinearities.leaky_rectify,
            W=lasagne.init.GlorotUniform())

    # We'll now add dropout of 50%:
    l_hid1_drop = lasagne.layers.DropoutLayer(l_hid1, p=0.5)

    # Another 800-unit layer:
    l_hid2 = lasagne.layers.DenseLayer(
            l_hid1_drop, num_units=800,
            nonlinearity=lasagne.nonlinearities.leaky_rectify)

     # Another 800-unit layer:
    l_hid3 = lasagne.layers.DenseLayer(
            l_hid2, num_units=800,
            nonlinearity=lasagne.nonlinearities.leaky_rectify)

    # 50% dropout again:
    l_hid2_drop = lasagne.layers.DropoutLayer(l_hid3, p=0.5)

    # Finally, we'll add the fully-connected output layer, of 10 softmax units:
    l_out = lasagne.layers.DenseLayer(
            l_hid2_drop, num_units=num_categories,
            nonlinearity=lasagne.nonlinearities.softmax)

    # Each layer is linked to its incoming layer(s), so we only need to pass
    # the output layer to give access to a network in Lasagne:
    return l_out


def build_custom_mlp(input_var=None, depth=2, width=800, drop_input=.2,
                     drop_hidden=.5):
    # By default, this creates the same network as `build_mlp`, but it can be
    # customized with respect to the number and size of hidden layers. This
    # mostly showcases how creating a network in Python code can be a lot more
    # flexible than a configuration file. Note that to make the code easier,
    # all the layers are just called `network` -- there is no need to give them
    # different names if all we return is the last one we created anyway; we
    # just used different names above for clarity.

    # Input layer and dropout (with shortcut `dropout` for `DropoutLayer`):
    network = lasagne.layers.InputLayer(shape=(None, 1, 224, 224),
                                        input_var=input_var)
    if drop_input:
        network = lasagne.layers.dropout(network, p=drop_input)
    # Hidden layers and dropout:
    nonlin = lasagne.nonlinearities.rectify
    for _ in range(depth):
        network = lasagne.layers.DenseLayer(
                network, width, nonlinearity=nonlin)
        if drop_hidden:
            network = lasagne.layers.dropout(network, p=drop_hidden)
    # Output layer:
    softmax = lasagne.nonlinearities.softmax
    network = lasagne.layers.DenseLayer(network, 10, nonlinearity=softmax)
    return network


def build_cnn_old_sketch(input_var=None):
    # As a third model, we'll create a CNN of two convolution + pooling stages
    # and a fully-connected hidden layer in front of the output layer.

    # Input layer, as usual:
    network = lasagne.layers.InputLayer(shape=(None, 1, 96, 96),
                                        input_var=input_var)
    # This time we do not apply input dropout, as it tends to work less well
    # for convolutional layers.

    # Convolutional layer with 32 kernels of size 5x5. Strided and padded
    # convolutions are supported as well; see the docstring.
    network = lasagne.layers.Conv2DLayer(
            network, num_filters=32, filter_size=(3, 3),
            nonlinearity=lasagne.nonlinearities.rectify,
            W=lasagne.init.GlorotUniform())
    # Expert note: Lasagne provides alternative convolutional layers that
    # override Theano's choice of which implementation to use; for details
    # please see http://lasagne.readthedocs.org/en/latest/user/tutorial.html.

    # Max-pooling layer of factor 2 in both dimensions:
    network = lasagne.layers.MaxPool2DLayer(network, pool_size=(2, 2))
    #network = lasagne.layers.NonlinearityLayer(network, nonlinearity=lasagne.nonlinearities.rectify)

    # Another convolution with 32 5x5 kernels, and another 2x2 pooling:
    # network = lasagne.layers.Conv2DLayer(
    #         network, num_filters=50, filter_size=(5, 5),
    #         nonlinearity=lasagne.nonlinearities.rectify)
    # network = lasagne.layers.MaxPool2DLayer(network, pool_size=(3, 3), stride=2)

    network = lasagne.layers.Conv2DLayer(
             network, num_filters=64, filter_size=(2, 2),
             nonlinearity=lasagne.nonlinearities.rectify)

    network = lasagne.layers.MaxPool2DLayer(network, pool_size=(2, 2))

    network = lasagne.layers.Conv2DLayer(
             network, num_filters=128, filter_size=(2, 2),
             nonlinearity=lasagne.nonlinearities.rectify)

    network = lasagne.layers.MaxPool2DLayer(network, pool_size=(2, 2))

    # A fully-connected layer of 256 units with 50% dropout on its inputs:
    network = lasagne.layers.DenseLayer(
            lasagne.layers.dropout(network, p=.3),
            num_units=500,
            nonlinearity=lasagne.nonlinearities.rectify)

    network = lasagne.layers.DenseLayer(
            lasagne.layers.dropout(network, p=.5),
            num_units=500,
            nonlinearity=lasagne.nonlinearities.rectify)

    # And, finally, the 10-unit output layer with 50% dropout on its inputs:
    network = lasagne.layers.DenseLayer(
            lasagne.layers.dropout(network, p=.5),
            num_units=250,
            nonlinearity=lasagne.nonlinearities.softmax)

    return network

def build_cnn(input_var=None):
    # As a third model, we'll create a CNN of two convolution + pooling stages
    # and a fully-connected hidden layer in front of the output layer.

    # Input layer, as usual:
    network = lasagne.layers.InputLayer(shape=(None, 1, 126, 126),
                                        input_var=input_var)
    # This time we do not apply input dropout, as it tends to work less well
    # for convolutional layers.

    # Convolutional layer with 32 kernels of size 5x5. Strided and padded
    # convolutions are supported as well; see the docstring.
    network = lasagne.layers.Conv2DLayer(
            network, num_filters=42, filter_size=(3, 3),
            nonlinearity=lasagne.nonlinearities.elu,
            W=lasagne.init.GlorotUniform())
    # Expert note: Lasagne provides alternative convolutional layers that
    # override Theano's choice of which implementation to use; for details
    # please see http://lasagne.readthedocs.org/en/latest/user/tutorial.html.

    # Max-pooling layer of factor 2 in both dimensions:
    network = lasagne.layers.MaxPool2DLayer(network, pool_size=(2, 2))
    #network = lasagne.layers.NonlinearityLayer(network, nonlinearity=lasagne.nonlinearities.rectify)

    # Another convolution with 32 5x5 kernels, and another 2x2 pooling:
    # network = lasagne.layers.Conv2DLayer(
    #         network, num_filters=50, filter_size=(5, 5),
    #         nonlinearity=lasagne.nonlinearities.rectify)
    # network = lasagne.layers.MaxPool2DLayer(network, pool_size=(3, 3), stride=2)

    network = lasagne.layers.Conv2DLayer(
             network, num_filters=63, filter_size=(2, 2),
             nonlinearity=lasagne.nonlinearities.elu)

    network = lasagne.layers.MaxPool2DLayer(network, pool_size=(2, 2))

    network = lasagne.layers.Conv2DLayer(
             network, num_filters=126, filter_size=(2, 2),
             nonlinearity=lasagne.nonlinearities.elu)

    network = lasagne.layers.MaxPool2DLayer(network, pool_size=(2, 2))

    network = lasagne.layers.ParametricRectifierLayer(network)

    # - Experimental layer
    network = lasagne.layers.Conv2DLayer(
             network, num_filters=126, filter_size=(2, 2),
             nonlinearity=lasagne.nonlinearities.elu)

    network = lasagne.layers.Conv2DLayer(
             network, num_filters=252, filter_size=(2, 2),
             nonlinearity=lasagne.nonlinearities.elu)

    network = lasagne.layers.ParametricRectifierLayer(network)

    network = lasagne.layers.Conv2DLayer(
             network, num_filters=504, filter_size=(1, 1),
             nonlinearity=lasagne.nonlinearities.elu)
    #network = lasagne.layers.MaxPool2DLayer(network, pool_size=(2, 2))
    # - End experiment layer

    # # A fully-connected layer of 256 units with 50% dropout on its inputs:
    # network = lasagne.layers.DenseLayer(
    #         lasagne.layers.dropout(network, p=.3),
    #         num_units=500,
    #         nonlinearity=lasagne.nonlinearities.rectify)
    #
    # network = lasagne.layers.DenseLayer(
    #         lasagne.layers.dropout(network, p=.5),
    #         num_units=500,
    #         nonlinearity=lasagne.nonlinearities.rectify)
    #
    network = lasagne.layers.DenseLayer(
            lasagne.layers.dropout(network, p=.3),
            num_units=2500,
            nonlinearity=lasagne.nonlinearities.elu)

    '''
    Experimental layer - just to check accuracy (remove if necessary)
    '''
    network = lasagne.layers.DenseLayer(
            lasagne.layers.dropout(network, p=.3),
            num_units=1024,
            nonlinearity=lasagne.nonlinearities.elu)


    # And, finally, the 10-unit output layer with 50% dropout on its inputs:
    network = lasagne.layers.DenseLayer(
            lasagne.layers.dropout(network, p=.5),
            num_units=num_categories,
            nonlinearity=lasagne.nonlinearities.softmax)

    return network


# BLVC Googlenet, model from the paper:
# "Going Deeper with Convolutions"
# Original source:
# https://github.com/BVLC/caffe/tree/master/models/bvlc_googlenet
# License: unrestricted use

# Download pretrained weights from:
# https://s3.amazonaws.com/lasagne/recipes/pretrained/imagenet/blvc_googlenet.pkl

from lasagne.layers import InputLayer
from lasagne.layers import DenseLayer
from lasagne.layers import ConcatLayer
from lasagne.layers import NonlinearityLayer
from lasagne.layers import GlobalPoolLayer
from lasagne.layers.conv import Conv2DLayer as ConvLayer
from lasagne.layers.pool import MaxPool2DLayer as PoolLayerDNN
from lasagne.layers import MaxPool2DLayer as PoolLayer
from lasagne.layers import LocalResponseNormalization2DLayer as LRNLayer
from lasagne.nonlinearities import softmax, linear


def build_inception_module(name, input_layer, nfilters):
    # nfilters: (pool_proj, 1x1, 3x3_reduce, 3x3, 5x5_reduce, 5x5)
    net = {}
    net['pool'] = PoolLayerDNN(input_layer, pool_size=3, stride=1, pad=1)
    net['pool_proj'] = ConvLayer(net['pool'], nfilters[0], 1)

    net['1x1'] = ConvLayer(input_layer, nfilters[1], 1)

    net['3x3_reduce'] = ConvLayer(input_layer, nfilters[2], 1)
    net['3x3'] = ConvLayer(net['3x3_reduce'], nfilters[3], 3, pad=1)

    net['5x5_reduce'] = ConvLayer(input_layer, nfilters[4], 1)
    net['5x5'] = ConvLayer(net['5x5_reduce'], nfilters[5], 5, pad=2)

    net['output'] = ConcatLayer([
        net['1x1'],
        net['3x3'],
        net['5x5'],
        net['pool_proj'],
        ])

    return {'{}/{}'.format(name, k): v for k, v in net.items()}


def build_model_LeNet(input_var=None):
    net = {}
    net['input'] = InputLayer(shape=(None, 1, 224, 224), input_var=input_var)
    net['conv1/7x7_s2'] = ConvLayer(net['input'], 64, 7, stride=2, pad=3)
    net['pool1/3x3_s2'] = PoolLayer(net['conv1/7x7_s2'],
                                    pool_size=3,
                                    stride=2,
                                    ignore_border=False)
    net['pool1/norm1'] = LRNLayer(net['pool1/3x3_s2'], alpha=0.00002, k=1)
    net['conv2/3x3_reduce'] = ConvLayer(net['pool1/norm1'], 64, 1)
    net['conv2/3x3'] = ConvLayer(net['conv2/3x3_reduce'], 192, 3, pad=1)
    net['conv2/norm2'] = LRNLayer(net['conv2/3x3'], alpha=0.00002, k=1)
    net['pool2/3x3_s2'] = PoolLayer(net['conv2/norm2'], pool_size=3, stride=2)

    net.update(build_inception_module('inception_3a',
                                      net['pool2/3x3_s2'],
                                      [32, 64, 96, 128, 16, 32]))
    net.update(build_inception_module('inception_3b',
                                      net['inception_3a/output'],
                                      [64, 128, 128, 192, 32, 96]))
    net['pool3/3x3_s2'] = PoolLayer(net['inception_3b/output'],
                                    pool_size=3, stride=2)

    net.update(build_inception_module('inception_4a',
                                      net['pool3/3x3_s2'],
                                      [64, 192, 96, 208, 16, 48]))
    net.update(build_inception_module('inception_4b',
                                      net['inception_4a/output'],
                                      [64, 160, 112, 224, 24, 64]))
    net.update(build_inception_module('inception_4c',
                                      net['inception_4b/output'],
                                      [64, 128, 128, 256, 24, 64]))
    net.update(build_inception_module('inception_4d',
                                      net['inception_4c/output'],
                                      [64, 112, 144, 288, 32, 64]))
    net.update(build_inception_module('inception_4e',
                                      net['inception_4d/output'],
                                      [128, 256, 160, 320, 32, 128]))
    net['pool4/3x3_s2'] = PoolLayer(net['inception_4e/output'],
                                    pool_size=3, stride=2)

    net.update(build_inception_module('inception_5a',
                                      net['pool4/3x3_s2'],
                                      [128, 256, 160, 320, 32, 128]))
    net.update(build_inception_module('inception_5b',
                                      net['inception_5a/output'],
                                      [128, 384, 192, 384, 48, 128]))

    net['pool5/7x7_s1'] = GlobalPoolLayer(net['inception_5b/output'])
    net['loss3/classifier'] = DenseLayer(net['pool5/7x7_s1'],
                                         num_units=num_categories,
                                         nonlinearity=linear)
    net['prob'] = NonlinearityLayer(net['loss3/classifier'],
                                    nonlinearity=softmax)
    #print(net)
    return net['prob']

def build_model_vgg(input_var=None):
    from lasagne.layers import DropoutLayer
    net = {}

    net['input'] = InputLayer((None, 1, 224, 224), input_var=input_var)
    net['conv1'] = ConvLayer(net['input'],
                             num_filters=96,
                             filter_size=7,
                             stride=2)
    # caffe has alpha = alpha * pool_size
   # net['norm1'] = NormLayer(net['conv1'], alpha=0.0001)
    net['pool1'] = PoolLayer(net['conv1'],
                             pool_size=3,
                             stride=3,
                             ignore_border=False)
    net['conv2'] = ConvLayer(net['pool1'], num_filters=256, filter_size=5)
    net['pool2'] = PoolLayer(net['conv2'],
                             pool_size=2,
                             stride=2,
                             ignore_border=False)
    net['conv3'] = ConvLayer(net['pool2'],
                             num_filters=512,
                             filter_size=3,
                             pad=1)
    net['conv4'] = ConvLayer(net['conv3'],
                             num_filters=512,
                             filter_size=3,
                             pad=1)
    net['conv5'] = ConvLayer(net['conv4'],
                             num_filters=512,
                             filter_size=3,
                             pad=1)
    net['pool5'] = PoolLayer(net['conv5'],
                             pool_size=3,
                             stride=3,
                             ignore_border=False)
    net['fc6'] = DenseLayer(net['pool5'], num_units=4096)
    net['drop6'] = DropoutLayer(net['fc6'], p=0.5)
    net['fc7'] = DenseLayer(net['drop6'], num_units=4096)
    net['drop7'] = DropoutLayer(net['fc7'], p=0.5)
    net['fc8'] = DenseLayer(net['drop7'], num_units=num_categories, nonlinearity=None)
    net['prob'] = NonlinearityLayer(net['fc8'], softmax)

    return net['prob']


def build_model_vgg16(input_var=None):
    net = {}
    net['input'] = InputLayer((None, 1, 224, 224), input_var=input_var)
    net['conv1_1'] = ConvLayer(net['input'], 64, 3, pad=1)
    net['conv1_2'] = ConvLayer(net['conv1_1'], 64, 3, pad=1)
    net['pool1'] = PoolLayer(net['conv1_2'], 2)
    net['conv2_1'] = ConvLayer(net['pool1'], 128, 3, pad=1)
    net['conv2_2'] = ConvLayer(net['conv2_1'], 128, 3, pad=1)
    net['pool2'] = PoolLayer(net['conv2_2'], 2)
    net['conv3_1'] = ConvLayer(net['pool2'], 256, 3, pad=1)
    net['conv3_2'] = ConvLayer(net['conv3_1'], 256, 3, pad=1)
    net['conv3_3'] = ConvLayer(net['conv3_2'], 256, 3, pad=1)
    net['pool3'] = PoolLayer(net['conv3_3'], 2)
    net['conv4_1'] = ConvLayer(net['pool3'], 512, 3, pad=1)
    net['conv4_2'] = ConvLayer(net['conv4_1'], 512, 3, pad=1)
    net['conv4_3'] = ConvLayer(net['conv4_2'], 512, 3, pad=1)
    net['pool4'] = PoolLayer(net['conv4_3'], 2)
    net['conv5_1'] = ConvLayer(net['pool4'], 512, 3, pad=1)
    net['conv5_2'] = ConvLayer(net['conv5_1'], 512, 3, pad=1)
    net['conv5_3'] = ConvLayer(net['conv5_2'], 512, 3, pad=1)
    net['pool5'] = PoolLayer(net['conv5_3'], 2)
    net['fc6'] = DenseLayer(net['pool5'], num_units=4096)
    net['fc7'] = DenseLayer(net['fc6'], num_units=4096)
    net['fc8'] = DenseLayer(net['fc7'], num_units=num_categories, nonlinearity=None)
    net['prob'] = NonlinearityLayer(net['fc8'], softmax)

    return net['prob']
# ############################# Batch iterator ###############################
# This is just a simple helper function iterating over training data in
# mini-batches of a particular size, optionally in random order. It assumes
# data is available as numpy arrays. For big datasets, you could load numpy
# arrays as memory-mapped files (np.load(..., mmap_mode='r')), or write your
# own custom data iteration function. For small datasets, you can also copy
# them to GPU at once for slightly improved performance. This would involve
# several changes in the main program, though, and is not demonstrated here.

def iterate_minibatches(inputs, targets, batchsize, shuffle=False):
    assert len(inputs) == len(targets)
    if shuffle:
        indices = np.arange(len(inputs))
        np.random.shuffle(indices)
    for start_idx in range(0, len(inputs) - batchsize + 1, batchsize):
        if shuffle:
            excerpt = indices[start_idx:start_idx + batchsize]
        else:
            excerpt = slice(start_idx, start_idx + batchsize)
        yield inputs[excerpt], targets[excerpt]


class SketchNet:
    def recognize_Image(self, filePath):
        print("recognize....")
        print(filePath)
        from skimage import filters
        from skimage import color
        startTime = time.time()

        img = io.imread(filePath,as_grey=True)
        #img = io.imread('airplane2.jpg')
        #img = filters.sobel(img)
        img = color.rgb2grey(img)

        img = sp.imresize(img, (224, 224))
        #img = img - np.median(img,axis=None,out=None,overwrite_input=False,keepdims=True)
        img = img - np.mean(img)
        # np.mean(img,axis=None,dtype=None,out=None,keepdims=True)
        #to save features
        # import matplotlib.pyplot as plt
        # plt.imsave('feature.jpg', img)
        # img = img.astype(dtype='uint8')
        # img = img.astype(dtype='float64')
        #end save
           # imgflip = np.fliplr(img)
        #io.imshow(img)
        img = img.flatten()

        data = []
        data.append(img)
        feature = np.array(data)
        feature = feature.reshape((-1, 1, 224, 224))
        output = lasagne.layers.get_output(self.network,feature,deterministic=True)
        #output = np.array(output)
        classes = np.argmax(output.eval())
        duration = time.time() - startTime
        print(categories[classes])
        print(duration)
        return categories[classes]

    def __init__(self):
        model = 'cnn'
        # Load the dataset
        self.load = False
        print("Loading data...")
        X_train, y_train, X_val, y_val, X_test, y_test = load_dataset()

        # Prepare Theano variables for inputs and targets
        input_var = T.tensor4('inputs')
        target_var = T.ivector('targets')

        # Create neural network model (depending on first command line parameter)
        print("Loading model...")
        #network = build_cnn(input_var)
        network = build_model_vgg16(input_var)
        #load weights from file
        param = np.load('model_VGG.npz')
        lasagne.layers.set_all_param_values(network, param['arr_0'])
        print("loaded paramters from saved model")
        self.load = True

        # Create a loss expression for training, i.e., a scalar objective we want
        # to minimize (for our multi-class problem, it is the cross-entropy loss):
        prediction = lasagne.layers.get_output(network)
        loss = lasagne.objectives.categorical_crossentropy(prediction, target_var)
        loss = loss.mean()
        # We could add some weight decay as well here, see lasagne.regularization.

        # Create update expressions for training, i.e., how to modify the
        # parameters at each training step. Here, we'll use Stochastic Gradient
        # Descent (SGD) with Nesterov momentum, but Lasagne offers plenty more.
        params = lasagne.layers.get_all_params(network, trainable=True)
        #updates = lasagne.updates.adagrad(loss, params, learning_rate=0.001)
        #updates = lasagne.updates.adam(loss, params, learning_rate=0.01)
        updates = lasagne.updates.nesterov_momentum(loss, params, learning_rate=0.001, momentum=0.9)

        # Create a loss expression for validation/testing. The crucial difference
        # here is that we do a deterministic forward pass through the network,
        # disabling dropout layers.
        test_prediction = lasagne.layers.get_output(network, deterministic=True)
        test_loss = lasagne.objectives.categorical_crossentropy(test_prediction,
                                                                target_var)
        test_loss = test_loss.mean()
        # As a bonus, also create an expression for the classification accuracy:
        test_acc = T.mean(T.eq(T.argmax(test_prediction, axis=1), target_var),
                          dtype=theano.config.floatX)

        # Compile a function performing a training step on a mini-batch (by giving
        # the updates dictionary) and returning the corresponding training loss:
        train_fn = theano.function([input_var, target_var], loss, updates=updates)

        # Compile a second function computing the validation loss and accuracy:
        val_fn = theano.function([input_var, target_var], [test_loss, test_acc])

        self.network = network

s = zerorpc.Server(SketchNet())
s.bind("tcp://0.0.0.0:4242")
s.run()

# testStart = SketchNet()

# if testStart.load:
    # testStart.recognize_Image('data/png/airplane/70.png')
    # testStart.recognize_Image('data/png/airplane/79.png')
    # testStart.recognize_Image('data/png/alarm clock/100.png')
    # testStart.recognize_Image('data/png/alarm clock/120.png')
    # testStart.recognize_Image('data/png/angel/170.png')
    # testStart.recognize_Image('data/png/angel/190.png')
    # testStart.recognize_Image('data/png/ant/277.png')
    # testStart.recognize_Image('data/png/ant/299.png')
    # testStart.recognize_Image('data/png/apple/335.png')
    # testStart.recognize_Image('data/png/apple/381.png')
    # testStart.recognize_Image('data/png/arm/440.png')
    # testStart.recognize_Image('data/png/arm/480.png')
    # testStart.recognize_Image('data/png/armchair/520.png')
    # testStart.recognize_Image('data/png/armchair/559.png')
    # testStart.recognize_Image('data/png/ashtray/611.png')
    # testStart.recognize_Image('data/png/ashtray/635.png')
    # testStart.recognize_Image('data/png/axe/685.png')
    # testStart.recognize_Image('data/png/axe/714.png')
    # testStart.recognize_Image('data/png/backpack/733.png')
    # testStart.recognize_Image('data/png/backpack/777.png')
