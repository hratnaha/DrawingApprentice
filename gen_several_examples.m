function examples = gen_several_examples(G, n)
    lib = loadlib;
    [examples,types] = task_generate_exemplars_1overk(G,lib,n);
    % Show samples
    figure;
    sz = [428,622];
    pos = get(gcf,'Position');
    pos(3:4) = sz;
    'wow'
    set(gcf,'Position',pos);
    nrow = ceil(sqrt(n));
    subplot(nrow+1,nrow,floor((nrow+1)/2)); 
    plot_image_only(G.img);
    title('original');
    for i=1:n
       subplot(nrow+1,nrow,i+nrow); 
       I = examples{i}.pimg > 0.5;
       %savefig(I);
       plot_image_only(I);
       if i==1
           title('new exemplars');
       end
    end
    
    