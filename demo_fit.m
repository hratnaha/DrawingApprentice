% Demo of fitting a motor program to an image.

% Parameters
K = 5; % number of unique parses we want to collect
verbose = true; % describe progress and visualize parse?
include_mcmc = true; % run mcmc to estimate local variability?
fast_mode = true; % skip the slow step of fitting strokes to details of the ink?

if fast_mode
    fprintf(1,'Fast mode skips the slow step of fitting strokes to details of the ink.\n');
    warning_mode('Fast mode is for demo purposes only and was not used in paper results.');
end


load('newpic.mat','newPic');
% % % fin = imresize(fin, [224 224]);
% %newPic = logical(~newPic);
% % load('mnist.mat', 'arr')
% 
% newImage = imresize(newPic, [105, 105]);
% % level = graythresh(newImage);
% % newPic = im2bw(newImage, level);
newPic = logical(~newPic);
G = fit_motorprograms(newPic,K,verbose,include_mcmc,fast_mode);
save('picasso_fit_G.mat','G')
gen_several_examples(G,5);
% % load('cross_img','img');
% % G = fit_motorprograms(img,K,verbose,include_mcmc,fast_mode);
% %load('output_fit_G.mat','G');
% % 
% gen_several_examples(G, 5)
% %copy this saved file and use it in demo_generate_exemplar
% load('0_1.mat', 'fin');
% zero = fin;
% 
% load('1_1.mat', 'fin');
% one = fin;
% 
% load('2_1.mat', 'fin');
% two = fin;
% 
% load('3_1.mat', 'fin');
% three = fin;
% 
% load('4_1.mat', 'fin');
% four = fin;
% 
% load('5_1.mat', 'fin');
% five = fin;
% 
% load('6_1.mat', 'fin');
% six = fin;
% 
% load('7_1.mat', 'fin');
% seven = fin;
% 
% load('8_1.mat', 'fin');
% eight = fin;
% 
% load('9_1.mat', 'fin');
% nine = fin;
% thinnedImage = bwmorph(nine, 'thin');
% imshow(thinnedImage);
% G = fit_motorprograms(thinnedImage,K,verbose,include_mcmc,fast_mode);
% gen_several_examples(G, 5);